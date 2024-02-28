#include <stdbool.h>
#include <linux/bpf.h>
#include <bpf/bpf_helpers.h>
#include <linux/if_ether.h>
#include <linux/in.h>
#include <linux/ip.h>
#include <linux/tcp.h>
#include <linux/udp.h>

#define NO_OF_CHARS 256
#define MAX_PAT_LEN 10
#define FOUND_STATE 99

struct 
{
    __uint(type, BPF_MAP_TYPE_ARRAY);
    __type(key, __u32);
    __type(value, __u32);
    __uint(max_entries, 1);
} drop_count SEC(".maps");

struct 
{
    __uint(type, BPF_MAP_TYPE_ARRAY);
    __type(key, __u32);
    __type(value, __u8);
    __uint(max_entries, NO_OF_CHARS*MAX_PAT_LEN);
} search_pattern SEC(".maps");

typedef struct context 
{
    __u8 search_state;
    struct xdp_md *ctx;
    __u16 index;
    bool found;
} context_t;

// https://stackoverflow.com/questions/77967675/is-it-possible-to-access-the-packet-when-using-bpf-loop
static int do_check(__u32 index, context_t *search_ctx)
{
    __u8 *data = (__u8 *)(long)search_ctx->ctx->data;
    __u8 *data_end = (__u8 *)(long)search_ctx->ctx->data_end;
    __u8 *this_byte = data + search_ctx->index++;

    if (this_byte >= data && this_byte+1 <= data_end)
    {
        __u32 key = (search_ctx->search_state * NO_OF_CHARS) + *this_byte;
        __u8* next_state = bpf_map_lookup_elem(&search_pattern, &key);

        if (next_state == NULL)
        {
            return 1;
        }
        else
        {
            if (*next_state == FOUND_STATE)
            {
                search_ctx->found = true;
                return 1;
            }
            search_ctx->search_state = *next_state;
        }
    }

    return 0;
}

SEC("drop_packets_matching_pattern")
int drop_packets_matching_pattern_parser_func(struct xdp_md *ctx)
{
    void *data_end = (void *)(long)ctx->data_end;
    void *data = (void *)(long)ctx->data;
    
    __u32 key = 0;
    __u32 *value;

    // If there isn't enough data for an Ethernet and IP header
    if (data + sizeof(struct ethhdr) + sizeof(struct iphdr) < data_end)
    {
        struct ethhdr *eth = data;
    
         // Is it an IP packet?
        if (eth->h_proto == __constant_htons(ETH_P_IP)) 
        {
            struct iphdr *ip = data + sizeof(struct ethhdr);

            ///////////////////////
            // START - for testing
            /*
            if (ip->protocol != IPPROTO_UDP) return XDP_PASS;
            if (data + sizeof(struct ethhdr) + sizeof(struct iphdr) + sizeof(struct udphdr) >= data_end) return XDP_PASS;
            struct udphdr *udp = (struct udphdr *)(data + sizeof(struct ethhdr) + sizeof(struct iphdr));
            if (udp->dest != __constant_htons(5555)) return XDP_PASS;
            */
            // END - for testing
            ///////////////////////

            if (ip->protocol == IPPROTO_UDP || ip->protocol == IPPROTO_TCP)
            {
                context_t search_ctx = (context_t) 
                {
                    .search_state = 0,
                    .ctx = ctx,
                    .index = 0,
                    .found = false
                };
                
                bpf_loop(data_end - data, do_check, &search_ctx, 0);

                if (search_ctx.found)
                {
                    value = bpf_map_lookup_elem(&drop_count, &key);
                    if (value)
                    {
                        *value += 1;
                    }
                    return XDP_DROP;
                }
            }
        }
    }
    return XDP_PASS;
}
