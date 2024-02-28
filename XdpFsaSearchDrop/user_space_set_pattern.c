#include <stdbool.h>
#include <stdio.h>
#include <unistd.h>
#include <bpf/bpf.h>
#include <errno.h>
#define NO_OF_CHARS 256
#define MAX_PAT_LEN 10
#define FOUND_STATE 99

int map_fd_by_name(char *name)
{
    unsigned int id = 0;
    while (true)
    {
        struct bpf_map_info info = {};
        __u32 len = sizeof(info);

        if (bpf_map_get_next_id(id, &id)) 
        {
            if (errno != ENOENT) 
            {
                fprintf(stderr, "Error looking for BPF map: %s\n", strerror(errno));
            }
            return -1;
        }

        int fd = bpf_map_get_fd_by_id(id);
        if (fd < 0) 
        {
            fprintf(stderr, "Can't get map by id (%d): %s\n", id, strerror(errno));
            return -1;
        }

        if (bpf_obj_get_info_by_fd(fd, &info, &len)) 
        {
            fprintf(stderr, "Can't get map info (%d): %s\n", id, strerror(errno));
            return -1;
        }

        if (strncmp(name, info.name, BPF_OBJ_NAME_LEN)) 
        {
            close(fd);
            //printf("Ignoring: %s\n",info.name);
            continue;
        }
        //printf("Using: %s\n",info.name);
        return fd;
    }
    return -1;
}

/**
 * From https://www.geeksforgeeks.org/finite-automata-algorithm-for-pattern-searching/
*/
__u8 getNextState(char *pat, __u8 M, __u8 state, __u8 x)
{
    // If the character c is same as next character
    // in pattern,then simply increment state
    if (state < M && x == pat[state])
        return state+1 == M ? FOUND_STATE : state+1;

    // ns stores the result which is next state
    __u8 ns, i;

    // ns finally contains the longest prefix
    // which is also suffix in "pat[0..state-1]c"

    // Start from the largest possible value
    // and stop when you find a prefix which
    // is also suffix
    for (ns = state; ns > 0; ns--)
    {
        if (pat[ns-1] == x)
        {
            for (i = 0; i < ns-1; i++)
                if (pat[i] != pat[state-ns+1+i])
                    break;
            if (i == ns-1)
                return ns;
        }
    }

    return 0;
}
bool setTF(char *pat, int M, int map_fd)
{
    for (int state = 0; state <= M; ++state)
    {
        for (int x = 0; x < NO_OF_CHARS; ++x)
        {
            int key = state*NO_OF_CHARS + x;
            __u8 next_state = getNextState(pat, M, state, x);
            if (bpf_map_update_elem(map_fd, &key, &next_state, 0) != 0)
            {
                return false;
            }
        }
    }
    return true;
}

int main() 
{
    int return_code = 0;
    int map_count_fd = 0;
    int map_pattern_fd = 0;
    __u32 drop_count_key = 0;
    __u32 drop_count;

    ///////////////////
    // Print drop count
    map_count_fd = map_fd_by_name("drop_count");
    if (map_count_fd < 0)
    {
        fprintf(stderr, "Failed to find drop count map\n");
        return -1;
    }
    if (bpf_map_lookup_elem(map_count_fd, &drop_count_key, &drop_count) == 0)
    {
        printf("Drops: %u\n", drop_count);
    }
    else 
    {
        fprintf(stderr, "Error looking up element in BPF map\n");
    }


    ///////////////////
    // Get next pattern

    printf("Update pattern to search for: ");
    char pattern[MAX_PAT_LEN];
    fgets(pattern, MAX_PAT_LEN, stdin);
    size_t length = strlen(pattern);
    if (pattern[length-1] == 10)
    {
        length = length - 1;
    }

    if (length != 0)
    {
        map_pattern_fd = map_fd_by_name("search_pattern");
        if (map_pattern_fd < 0)
        {
            fprintf(stderr, "Failed to find search_pattern map\n");
            return_code = -1;
        }
        else if (!setTF(pattern, length, map_pattern_fd))
        {
            fprintf(stderr, "Failed to update search_pattern map\n");
            return_code = -1;
        }
        else
        {
            printf("Pattern updated\n");
            drop_count = 0;
            bpf_map_update_elem(map_count_fd, &drop_count_key, &drop_count, 0);
        }

    }

    if (map_count_fd > 0)
    {
        close(map_count_fd);
    }
    if (map_pattern_fd > 0)
    {
        close(map_pattern_fd);
    }
    return return_code;
}
