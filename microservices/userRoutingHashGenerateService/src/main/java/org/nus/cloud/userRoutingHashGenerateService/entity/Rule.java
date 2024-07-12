package org.nus.cloud.userRoutingHashGenerateService.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rule {

    private int startHash;

    private int endHash;

    private int index;
}
