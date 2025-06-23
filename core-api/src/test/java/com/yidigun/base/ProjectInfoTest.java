package com.yidigun.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectInfoTest {

    @Test
    public void testProjectInfo() {
        assertEquals(17, ProjectInfo.javaReleaseNo());
    }
}
