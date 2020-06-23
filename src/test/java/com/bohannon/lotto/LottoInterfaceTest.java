package com.bohannon.lotto;

import org.junit.Test;
import java.awt.*;
import static org.junit.Assert.*;

public class LottoInterfaceTest {
    @Test
    public void testSetWindowLocation() throws Exception {

    }

    @Test
    public void testMain() throws Exception {
        LottoInterface testInterface = new LottoInterface();
        assertNotNull(testInterface);
        System.out.println("The LottoInterface is not null.");

        Dimension testInterfaceSize = testInterface.getSize();
        assert testInterface.getSize().equals(testInterfaceSize);
        System.out.println("The interface size is correct: " + testInterfaceSize);
    }
}