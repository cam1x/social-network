package com.epam.model.aggregation;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.Month;

@Data
public class MaxFriendships {

    @Id
    private Month month;
    private int maxFriendships;
}
