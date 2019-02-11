package com.quanwei.network;

import com.quanwei.network.core.util.DataUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NetworkApplicationTests {

    @Test
    public void contextLoads() throws IOException {
        DataUtil bean = new DataUtil();
    }

}

