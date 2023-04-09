package com.epam.model.aggregation;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.DayOfWeek;

@Data
public class MessagesCount {

    @Id
    private DayOfWeek dayOfWeek;
    private int avgMsgCount;
}

