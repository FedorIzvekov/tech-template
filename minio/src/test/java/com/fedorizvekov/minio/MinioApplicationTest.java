package com.fedorizvekov.minio;

import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
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
