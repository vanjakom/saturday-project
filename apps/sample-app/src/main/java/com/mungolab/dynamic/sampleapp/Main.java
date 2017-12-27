package com.mungolab.dynamic.sampleapp;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello World Sample App");

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> result = objectMapper.readValue("{\"name\":\"test node\"}", Map.class);

        System.out.println("Name: " + result.get("name"));
    }
}
