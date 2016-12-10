package com.workshop;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertTrue;

@Slf4j
public class BCryptTest {

    @Test
    public void bCryptTest() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("123456");

        log.info(encode);

        assertTrue(passwordEncoder.matches("123456", encode));
    }
}
