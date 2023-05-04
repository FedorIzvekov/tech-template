package com.fedorizvekov.minio;

import static org.mockito.Mockito.mockStatic;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class MinioApplicationTest {

    private MockedStatic<MinioApplication> mockedStatic;

    @BeforeEach
    void setUp() {
        mockedStatic = mockStatic(MinioApplication.class);
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }


    @Test
    @DisplayName("When public static void main run context")
    void When_publicStaticVoidMain_runContext() {
        MinioApplication.main(new String[]{});

        mockedStatic.verify(() -> SpringApplication.run(MinioApplication.class, new String[]{}));
    }

}
