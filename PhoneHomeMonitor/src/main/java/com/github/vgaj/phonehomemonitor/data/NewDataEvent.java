package com.github.vgaj.phonehomemonitor.data;

import lombok.Data;
import lombok.NonNull;

/**
 * Some new data that was captured that needed to be queued for processing
 */
@Data
public class NewDataEvent
{
    // TODO: Can the data be stored here?
    private RemoteAddress host;
    private int length;
    private long epochMinute;

}
