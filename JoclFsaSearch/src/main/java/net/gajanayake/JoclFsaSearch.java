package net.gajanayake;

import static org.jocl.CL.*;
import org.jocl.*;

import java.util.ArrayList;
import java.util.Arrays;
import static java.lang.System.*;

public class JoclFsaSearch
{

    private static String programSource =
        "__kernel void "+
        "sampleKernel(__global const short *data,"+
        "             __global const short *lookup,"+
        "             __global const int *stateMachine,"+
        "             int patternLength,"+
        "             int uniqueCharCount,"+
        "             int dataChunkSize,"+
        "             int dataSize,"+    
        "             __global int *result)"+
        "{"+
        "    int gid = get_global_id(0);"+
        "    result[gid] = -1;"+

        "    int start = gid * dataChunkSize;"+
        "    if (gid > 0) { start = start - patternLength + 1; }"+
        "    if (start < 0) { start = 0; }"+

        "    int end = ((gid + 1) * dataChunkSize) - 1;"+
        "    if (end > dataSize) { end = dataSize; }"+

        "    int state = 0;"+
        "    for (int i = start; i <= end; i++)"+
        "    {"+
        "        short c = data[i];"+
        "        for (int j = 0; j < uniqueCharCount; j++)"+
        "        {"+
        "            if (lookup[j] == c)"+
        "            {"+
        "                state = stateMachine[ state*uniqueCharCount + j ];"+
        "                if (state == patternLength)"+
        "                {"+
        "                    result[gid] = i - patternLength + 1;"+
        "                    return;"+
        "                }"+
        "            }"+
        "        }"+
        "    }"+
        "}";

    private static String emptyProgram =
        "__kernel void "+
        "sampleKernel(__global const short *data,"+
        "             __global const short *lookup,"+
        "             __global const int *stateMachine,"+
        "             int patternLength,"+
        "             int uniqueCharCount,"+
        "             int dataChunkSize,"+
        "             int dataSize,"+    
        "             __global int *result)"+
        "{"+
        "    int gid = get_global_id(0);"+
        "    result[gid] = -1;"+
        "}";


    static int getNextState(char[] pat, int M, int state, char x) 
    { 
        // This implementation is from: https://www.geeksforgeeks.org/finite-automata-algorithm-for-pattern-searching/

        // If the character is same as next character in the pattern, then simply increment state 
        if(state < M && x == pat[state]) 
            return state + 1; 

        // ns stores the result which is next state 
        int ns, i; 

        // ns finally contains the longest prefix which is also suffix in "pat[0..state-1]c" 

        // Start from the largest possible value and stop when you find a prefix which is also suffix 
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

    static void computeTF(char[] pattern, int patternLength, char mapCharToIndex[], int uniqueCharCount, int numberOfStates, int stateMachine[]) 
    { 
        for (int state = 0; state < numberOfStates; state++) 
            for (int charNumber = 0; charNumber < uniqueCharCount; charNumber++) 
                stateMachine[state*uniqueCharCount + charNumber] = getNextState(pattern, numberOfStates, state, mapCharToIndex[charNumber]); 
    } 

    ////////////////////////////////////////////////////////////////////////////
    // MAIN
    ////////////////////////////////////////////////////////////////////////////
    public static void main(String args[])
    {
        // 5M
        System.out.println("\n******************\n*** 5M Real Kernel");
        char data[] = new char[5*1024*1024];
        String patternString = populateData(data);
        int firstMatch = search(data, patternString, true, true, true, true, 1);
        assert( firstMatch == 7);

        System.out.println("\n******************\n*** 5M Null Kernel");
        search(data, patternString, false, true, false, false, 1);
        System.out.println("");
        System.out.println("");

        // 50M
        System.out.println("\n*******************\n*** 50M Real Kernel");
        data = new char[50*1024*1024];
        patternString = populateData(data);
        firstMatch = search(data, patternString, false, true, true, true, 1);
        assert( firstMatch == 7);

        System.out.println("\n*******************\n*** 50M Real Kernel - Double chunk count");
        search(data, patternString, false, true, true, false, 2);

        System.out.println("\n*******************\n*** 50M Real Kernel - 10x chunk count");
        search(data, patternString, false, true, true, false, 10);

        System.out.println("\n*******************\n*** 50M Real Kernel - 100x chunk count");
        search(data, patternString, false, true, true, false, 100);

        System.out.println("\n*******************\n*** 50M Real Kernel - 1000x chunk count");
        search(data, patternString, false, true, true, false, 1000);

        System.out.println("\n*******************\n*** 50M Null Kernel");
        search(data, patternString, false, true, false, false, 1);
    }

    private static String populateData(char data[]) {
        for (int i=0; i<data.length; i++)
        {
            data[i] = 'a';
        }
        //abbbabaabba
        data[0]  = 'a';
        data[1]  = 'b';
        data[2]  = 'b';
        data[3]  = 'b';
        data[4]  = 'a';
        data[5]  = 'b';
        data[6]  = 'a';
        data[7]  = 'a'; // start
        data[8]  = 'b';
        data[9]  = 'b';
        data[10] = 'a';

        // Pattern to search for
        return "abba";
    }



    private static int search(char[] data, String patternString, boolean printSystemInfo, boolean printDetails, boolean useRealKernel, boolean checkOnCPU, double chunkCountToComputeUnitRatio)
    {
        CL.setExceptionsEnabled(true);

        final int platformIndex = 0;
        final long deviceType = CL_DEVICE_TYPE_GPU;
        final int deviceIndex = 0;

        // Obtain the number of platforms
        int numPlatformsArray[] = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain a platform ID
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);
        
        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];

        // Obtain a device ID 
        cl_device_id devices[] = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        // Get extra inforation
        String deviceName = getString(devices[0], CL_DEVICE_NAME);
        int computeUnitCount = getInt(device, CL_DEVICE_MAX_COMPUTE_UNITS);
        int clock = getInt(device, CL_DEVICE_MAX_CLOCK_FREQUENCY);
        int cacheline = getInt(device, CL_DEVICE_GLOBAL_MEM_CACHELINE_SIZE);
        long cache = getLong(device, CL_DEVICE_GLOBAL_MEM_CACHE_SIZE);
        long mem = getLong(device, CL_DEVICE_GLOBAL_MEM_SIZE);
        long local = getLong(device, CL_DEVICE_LOCAL_MEM_SIZE);
        long memAlloc = getLong(device, CL_DEVICE_MAX_MEM_ALLOC_SIZE);
        if (printSystemInfo)
        {
            System.out.println();
            System.out.printf("CL_DEVICE_NAME: %s\n", deviceName);
            System.out.printf("CL_DEVICE_MAX_COMPUTE_UNITS: %d\n", computeUnitCount);
            System.out.printf("CL_DEVICE_MAX_CLOCK_FREQUENCY: %d\n", clock);
            System.out.printf("CL_DEVICE_GLOBAL_MEM_CACHELINE_SIZE: %d\n", cacheline);
            System.out.printf("CL_DEVICE_GLOBAL_MEM_CACHE_SIZE: %d\n", cache);
            System.out.printf("CL_DEVICE_GLOBAL_MEM_SIZE: %d\n", mem);
            System.out.printf("CL_DEVICE_LOCAL_MEM_SIZE: %d\n", local);
            System.out.printf("CL_DEVICE_MAX_MEM_ALLOC_SIZE: %d\n", memAlloc);
        }
        System.out.println();
        
        char[] pattern = patternString.toCharArray();
        int patternLength = pattern.length;

        // Unique Characters in Pattern
        ArrayList<Character> uniqueCharList = new ArrayList<Character>();
        for (char c : pattern)
        {
            if (!uniqueCharList.contains(c))
            {
                uniqueCharList.add(c);
            }
        }
        int uniqueCharCount = uniqueCharList.size();        
        char mapCharToIndex[] = new char[uniqueCharCount];
        for (int i = 0; i < uniqueCharCount; i++)
        {
            mapCharToIndex[i] = uniqueCharList.get(i);
        }

        // State Machine
        int stateMachine[] = new int[patternLength * uniqueCharCount];
        Arrays.fill(stateMachine, 0);

        // State machine is:
        // In State 0: (get a), (get b)
        // In State 1: (get a), (get b)
        // In State 2: (get a), (get b)
        // In State 3: (get a), (get b)
        //
        // So for 'abba' it is:
        // stateMachine[0] = 1;
        // stateMachine[1] = 0;
        // stateMachine[2] = 1;
        // stateMachine[3] = 2;
        // stateMachine[4] = 1;
        // stateMachine[5] = 3;
        // stateMachine[6] = 4;
        // stateMachine[7] = 0;
        computeTF(pattern, patternLength, mapCharToIndex, uniqueCharCount, patternLength, stateMachine) ;

        int chunkCount = (int) (computeUnitCount * chunkCountToComputeUnitRatio);

        int result[] = new int[chunkCount];

        Pointer pData = Pointer.to(data);
        Pointer pMapCharToIndex = Pointer.to(mapCharToIndex);
        Pointer pStateMachine = Pointer.to(stateMachine);
        Pointer pResult = Pointer.to(result);

        int chunckSize = (data.length / chunkCount) + 1;
        if (printDetails) {
            System.out.println("Data Size: " + data.length);
        }
        System.out.println("Chunk Size: " + chunckSize);

        // Create a context for the selected device
        long start = currentTimeMillis();
        cl_context context = clCreateContext(contextProperties, 1, new cl_device_id[]{device}, null, null, null);
        
        // Create a command-queue for the selected device
        cl_queue_properties properties = new cl_queue_properties();
        cl_command_queue commandQueue = clCreateCommandQueueWithProperties(context, device, properties, null);
        if (printDetails) {
            System.out.printf("Context and Command Queue creation took %d ms\n", (currentTimeMillis() - start));
        }
        

        // Create the program from the source code
        start = currentTimeMillis();
        cl_program program = clCreateProgramWithSource(context, 1, new String[]{ useRealKernel ? programSource : emptyProgram }, null, null);
        if (printDetails) {
            System.out.printf("Program creation took %d ms\n", (currentTimeMillis() - start));
        }
        
        // Build the program
        start = currentTimeMillis();
        clBuildProgram(program, 0, null, null, null, null);
        if (printDetails) {
            System.out.printf("Program builing took %d ms\n", (currentTimeMillis() - start));
        }
        
        // Create the kernel
        start = currentTimeMillis();
        cl_kernel kernel = clCreateKernel(program, "sampleKernel", null);
        if (printDetails) {
            System.out.printf("Kernel creation took %d ms\n", (currentTimeMillis() - start));
        }
        

        //////////
        long startFull = currentTimeMillis();

        // Allocate the memory objects for the input and output data
        start = currentTimeMillis();
        cl_mem memData = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_short * data.length, pData, null);
        cl_mem memLookup = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_short * mapCharToIndex.length, pMapCharToIndex, null);
        cl_mem memStateMachine = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * stateMachine.length, pStateMachine, null);
        cl_mem memResult = clCreateBuffer(context, CL_MEM_READ_WRITE, Sizeof.cl_int * result.length, null, null);
        if (printDetails) {
            System.out.printf("Buffer creation took %d ms\n", (currentTimeMillis() - start));
        }

        // Set the arguments for the kernel
        start = currentTimeMillis();
        int a = 0;
        clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(memData));
        clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(memLookup));
        clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(memStateMachine));
        clSetKernelArg(kernel, a++, Sizeof.cl_int, Pointer.to(new int[]{patternLength}));
        clSetKernelArg(kernel, a++, Sizeof.cl_int, Pointer.to(new int[]{uniqueCharCount}));
        clSetKernelArg(kernel, a++, Sizeof.cl_int, Pointer.to(new int[]{chunckSize}));
        clSetKernelArg(kernel, a++, Sizeof.cl_int, Pointer.to(new int[]{data.length}));
        clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(memResult));
        if (printDetails) {
            System.out.printf("Args setting took %d ms\n", (currentTimeMillis() - start));
        }

        // Set the work-item dimensions
        start = currentTimeMillis();
        long global_work_size[] = new long[]{chunkCount};
        long local_work_size[] = new long[]{1};

        // Execute the kernel
        //clEnqueueNDRangeKernel(commandQueue, kernel, 1, null, global_work_size, null, 0, null, null);
        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null, global_work_size, local_work_size, 0, null, null);
        if (printDetails) {
            System.out.printf("Enqueuing took %d ms\n", (currentTimeMillis() - start));
        }

        // Read the output data
        start = currentTimeMillis();
        clEnqueueReadBuffer(commandQueue, memResult, CL_TRUE, 0, result.length * Sizeof.cl_int, pResult, 0, null, null);

        // Wait for execution
        int status = clFinish(commandQueue);
        if (status == CL_SUCCESS) {
            if (printDetails) {
                System.out.printf("Execute and result read took %d ms\n", (currentTimeMillis() - start));
            }
        } else if (status == CL_INVALID_COMMAND_QUEUE) {
            System.out.println("Error: command_queue is not a valid command-queue");
        } else if (status == CL_SUCCESS) {
            System.out.println("Error: failure to allocate resources required by the OpenCL implementation on the host");
        } else if (status == CL_SUCCESS) {
            System.out.println("Error:  failure to allocate resources required by the OpenCL implementation on the device");
        }

        // Release kernel, program, and memory objects
        start = currentTimeMillis();
        clReleaseMemObject(memData);
        clReleaseMemObject(memLookup);
        clReleaseMemObject(memStateMachine);
        clReleaseMemObject(memResult);
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);
        if (printDetails) {
            System.out.printf("Release took %d ms\n", (currentTimeMillis() - start));
        }

        System.out.printf("Total execution time on GPU %d ms\n", (currentTimeMillis() - startFull));

        // Get the result
        int firstMatch = -1;
        for (int i=0; i<result.length; i++)
        {
            int thisMatch = (int)result[i];
            if (firstMatch == -1 && thisMatch >= 0)
            {
                firstMatch = thisMatch;
            }
            //System.out.print(i + ":" + thisMatch + ", ");
        }
        //System.out.println();


        // Now try the same in a single thread in the CPU
        if (checkOnCPU) {
            start = currentTimeMillis();
            int firstMatchOnCpu = -1;
            int state = 0;
            for (int i=0; i<data.length; i++) {
                char c = data[i];
                for (int j=0; j<mapCharToIndex.length; j++) {
                    if (c == mapCharToIndex[j]) {

                        state = stateMachine [state*mapCharToIndex.length + j];
                        if (state == patternLength) {
                            if (firstMatchOnCpu == -1) {
                            firstMatchOnCpu = i - patternLength + 1;
                            }
                            // keep on looking to get a representative execution time
                            state = 0;
                        }
                    }
                }

            }
            assert (!useRealKernel || firstMatch == firstMatchOnCpu);
            System.out.printf("* Single threaded search on CPU %d ms\n", (currentTimeMillis() - start));
        }

        return firstMatch;
    }

    private static String getString(cl_device_id device, int paramName)
    {
        // Obtain the length of the string that will be queried
        long size[] = new long[1];
        clGetDeviceInfo(device, paramName, 0, null, size);

        // Create a buffer of the appropriate size and fill it with the info
        byte buffer[] = new byte[(int)size[0]];
        clGetDeviceInfo(device, paramName, buffer.length, Pointer.to(buffer), null);

        // Create a string from the buffer (excluding the trailing \0 byte)
        return new String(buffer, 0, buffer.length-1);
    }

    private static int getInt(cl_device_id device, int paramName)
    {
        int values[] = new int[1];
        clGetDeviceInfo(device, paramName, Sizeof.cl_int, Pointer.to(values), null);
        return values[0];
    }

    private static long getLong(cl_device_id device, int paramName)
    {
        long values[] = new long[1];
        clGetDeviceInfo(device, paramName, Sizeof.cl_long, Pointer.to(values), null);
        return values[0];
    }
        
}
