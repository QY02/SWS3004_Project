package org.cs304.backend;

import com.alibaba.fastjson2.JSONObject;
import org.cs304.backend.controller.AttachmentController;
import org.cs304.backend.entity.Attachment;
import org.cs304.backend.exception.ServiceException;
import org.cs304.backend.mapper.AttachmentMapper;
import org.cs304.backend.service.IAttachmentService;
import org.cs304.backend.service.impl.AttachmentServiceImpl;
import org.cs304.backend.utils.RedisUtil;
import org.cs304.backend.utils.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockitoAnnotations;
import org.springframework.aop.framework.AopContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
/**
 * This test class was generated with the assistance of AI tools, specifically GitHub Copilot.
 * GitHub Copilot uses advanced machine learning models to help developers by suggesting code snippets,
 * completing lines, and providing relevant code examples.
 *
 * The AI-generated code serves as a starting point and can significantly speed up the development process.
 * However, it's important to review, test, and potentially modify the generated code to ensure it meets
 * the specific requirements and best practices of your project.
 *
 * In this test class, the AI helped generate the test methods, setup and teardown logic, and other
 * auxiliary functions. The developer should ensure the correctness and completeness of these tests
 * by reviewing the generated code and making necessary adjustments.
 *
 * Example Usage:
 * - The AI can suggest boilerplate code for setting up a test environment.
 * - It can provide example test cases based on the developer's code context.
 * - It helps in maintaining consistency in the coding style and standards.
 *
 * Note: The integration of AI tools like GitHub Copilot is an augmentation of the development process,
 * and human oversight is crucial for the successful implementation and maintenance of the codebase.
 */
public class AttachmentServiceImplTest {

    @InjectMocks
    AttachmentServiceImpl attachmentService;

    @Mock
    private AttachmentMapper attachmentMapper;

    @Mock
    private RedisUtil redisUtil;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(attachmentService, "baseMapper", attachmentMapper);
    }

    @Test
    @DisplayName("Test getById")
    public void testGetById() {
        Exception exception = assertThrows(ServiceException.class, () -> {
            attachmentService.getById(0, null);
        });
        assertEquals("Invalid Attachment Id", exception.getMessage());

        exception = assertThrows(ServiceException.class, () -> {
            attachmentService.getById(1, 1);
        });
        assertEquals("Permission denied", exception.getMessage());

        when(attachmentMapper.selectById(anyInt())).thenReturn(null);

        exception = assertThrows(ServiceException.class, () -> {
            attachmentService.getById(0, 1);
        });
        assertEquals("Attachment not exist", exception.getMessage());

        when(attachmentMapper.selectById(anyInt())).thenReturn(new Attachment());
        when(redisUtil.generateAndAddFileToken(anyString())).thenReturn("");

        Attachment attachment = attachmentService.getById(0, 1);
        assertNotNull(attachment);
    }

    @Test
    @DisplayName("Test getBatchByIds")
    public void testGetBatchByIds() {
        when(attachmentMapper.selectById(anyInt())).thenReturn(new Attachment());
        when(redisUtil.generateAndAddFileToken(anyString())).thenReturn("");
        List<Attachment> attachmentList = attachmentService.getBatchByIds(0, List.of(1, 2, 3));
        assertFalse(attachmentList.isEmpty());
    }

    @Test
    @DisplayName("Test uploadStart")
    public void testUploadStart() {
        Exception exception = assertThrows(ServiceException.class, () -> {
            attachmentService.uploadStart(0, null, null);
        });
        assertEquals("Invalid file path", exception.getMessage());

        exception = assertThrows(ServiceException.class, () -> {
            attachmentService.uploadStart(0, "*", null);
        });
        assertEquals("Invalid file path", exception.getMessage());

        when(redisUtil.generateAndAddFileToken(anyString())).thenReturn("");

        JSONObject result = attachmentService.uploadStart(0, "a", null);
        assertNotNull(result);

        result = attachmentService.uploadStart(0, "a", new JSONObject());
        assertNotNull(result);
    }

    @Test
    @DisplayName("Test uploadFinish")
    public void testUploadFinish() {
        Exception exception = assertThrows(ServiceException.class, () -> {
            attachmentService.uploadFinish(0, null, null, false);
        });
        assertEquals("Invalid file path", exception.getMessage());

        exception = assertThrows(ServiceException.class, () -> {
            attachmentService.uploadFinish(0, "*", null, false);
        });
        assertEquals("Invalid file path", exception.getMessage());

        exception = assertThrows(ServiceException.class, () -> {
            attachmentService.uploadFinish(0, "a", null, false);
        });
        assertEquals("Permission denied", exception.getMessage());

        when(attachmentMapper.insert(any())).thenReturn(1);

        JSONObject result = attachmentService.uploadFinish(2, "a", null, false);
        assertNotNull(result);

        result = attachmentService.uploadFinish(2, "a", null, true);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Test uploadBatchStart")
    public void testUploadBatchStart() {
        Exception exception = assertThrows(ServiceException.class, () -> {
            attachmentService.uploadBatchStart(1, null, null, null);
        });
        assertEquals("Permission denied", exception.getMessage());

        exception = assertThrows(ServiceException.class, () -> {
            attachmentService.uploadBatchStart(0, null, null, null);
        });
        assertEquals("Invalid parameters", exception.getMessage());

        List<String> fileDirList = new ArrayList<>();
        fileDirList.add("a");
        fileDirList.add(null);
        List<String> fileNameList = new ArrayList<>();
        fileNameList.add("a.txt");
        fileNameList.add(null);
        exception = assertThrows(ServiceException.class, () -> {
            attachmentService.uploadBatchStart(0, fileDirList, fileNameList, null);
        });
        assertEquals("Invalid value in fileDirList at index 1", exception.getMessage());

        fileDirList.set(1, "*");
        exception = assertThrows(ServiceException.class, () -> {
            attachmentService.uploadBatchStart(0, fileDirList, fileNameList, null);
        });
        assertEquals("Invalid value in fileNameList at index 1", exception.getMessage());

        fileNameList.set(1, "b.txt");
        exception = assertThrows(ServiceException.class, () -> {
            attachmentService.uploadBatchStart(0, fileDirList, fileNameList, null);
        });
        assertEquals("Invalid parameters", exception.getMessage());

        fileDirList.set(1, "b");
        when(redisUtil.generateAndAddFileToken(anyString())).thenReturn("");

        JSONObject result = attachmentService.uploadBatchStart(0, fileDirList, fileNameList, null);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Test uploadBatchFinish")
    public void testUploadBatchFinish() {
        Exception exception = assertThrows(ServiceException.class, () -> {
            attachmentService.uploadBatchFinish(0, null, null);
        });
        assertEquals("Invalid file path list", exception.getMessage());

        mockStatic(AopContext.class);
        when(AopContext.currentProxy()).thenReturn(attachmentService);

        when(attachmentMapper.insert(any())).thenReturn(1);

        JSONObject result = attachmentService.uploadBatchFinish(2, List.of("a", "b", "c"), null);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Test deleteById")
    public void testDeleteById() {
        Exception exception = assertThrows(ServiceException.class, () -> {
            attachmentService.deleteById(1, null);
        });
        assertEquals("Permission denied", exception.getMessage());

        ReflectionTestUtils.setField(attachmentService, "fileServerHost", "");
        ReflectionTestUtils.setField(attachmentService, "fileServerPort", "");
        ReflectionTestUtils.setField(attachmentService, "adminToken", "");

        exception = assertThrows(ServiceException.class, () -> {
            attachmentService.deleteById(0, null);
        });
        assertEquals("This API is currently unavailable", exception.getMessage());

        ReflectionTestUtils.setField(attachmentService, "fileServerHost", "192.168.1.2");
        ReflectionTestUtils.setField(attachmentService, "fileServerPort", "25566");
        ReflectionTestUtils.setField(attachmentService, "adminToken", "abcdef");

        exception = assertThrows(ServiceException.class, () -> {
            attachmentService.deleteById(0, null);
        });
        assertEquals("Invalid Attachment Id", exception.getMessage());

        when(attachmentMapper.selectById(anyInt())).thenReturn(null);

        exception = assertThrows(ServiceException.class, () -> {
            attachmentService.deleteById(0, 1);
        });
        assertEquals("Attachment not exist", exception.getMessage());

        Attachment attachment = new Attachment();
        attachment.setFilePath("aaa");
        when(attachmentMapper.selectById(anyInt())).thenReturn(attachment);

        when(attachmentMapper.deleteById(anyInt())).thenReturn(1);

        ResponseEntity<Void> mockResponse500 = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        try (MockedConstruction<RestTemplate> ignored = mockConstruction(RestTemplate.class, (mock, context) -> when(mock.exchange(anyString(), any(), any(), eq(Void.class))).thenReturn(mockResponse500))) {
            exception = assertThrows(ServiceException.class, () -> {
                attachmentService.deleteById(0, 1);
            });
            assertEquals("An error occurred when communicating with the file server", exception.getMessage());
        }

        ResponseEntity<Void> mockResponse200 = new ResponseEntity<>(HttpStatus.OK);
        try (MockedConstruction<RestTemplate> ignored = mockConstruction(RestTemplate.class, (mock, context) -> when(mock.exchange(anyString(), any(), any(), eq(Void.class))).thenReturn(mockResponse200))) {
            attachmentService.deleteById(0, 1);
        }
    }

    @Test
    @DisplayName("Test deleteBatchByIdList")
    public void testDeleteBatchByIdList() {
        Exception exception = assertThrows(ServiceException.class, () -> {
            attachmentService.deleteBatchByIdList(1, null);
        });
        assertEquals("Permission denied", exception.getMessage());

        ReflectionTestUtils.setField(attachmentService, "fileServerHost", "");
        ReflectionTestUtils.setField(attachmentService, "fileServerPort", "");
        ReflectionTestUtils.setField(attachmentService, "adminToken", "");

        exception = assertThrows(ServiceException.class, () -> {
            attachmentService.deleteBatchByIdList(0, null);
        });
        assertEquals("This API is currently unavailable", exception.getMessage());

        ReflectionTestUtils.setField(attachmentService, "fileServerHost", "192.168.1.2");
        ReflectionTestUtils.setField(attachmentService, "fileServerPort", "25566");
        ReflectionTestUtils.setField(attachmentService, "adminToken", "abcdef");

        exception = assertThrows(ServiceException.class, () -> {
            attachmentService.deleteBatchByIdList(0, null);
        });
        assertEquals("Invalid id list", exception.getMessage());

        List<Integer> idList = new ArrayList<>();
        idList.add(null);
        exception = assertThrows(ServiceException.class, () -> {
            attachmentService.deleteBatchByIdList(0, idList);
        });
        assertEquals("Invalid id at index 0", exception.getMessage());

        idList.set(0, 1);
        when(attachmentMapper.selectById(anyInt())).thenReturn(null);

        exception = assertThrows(ServiceException.class, () -> {
            attachmentService.deleteBatchByIdList(0, idList);
        });
        assertEquals("Attachment with id = 1 not exist", exception.getMessage());

        Attachment attachment = new Attachment();
        attachment.setFilePath("aaa");
        when(attachmentMapper.selectById(anyInt())).thenReturn(attachment);

        when(attachmentMapper.deleteBatchIds(any())).thenReturn(1);

        ResponseEntity<Void> mockResponse500 = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        try (MockedConstruction<RestTemplate> ignored = mockConstruction(RestTemplate.class, (mock, context) -> when(mock.exchange(anyString(), any(), any(), eq(Void.class))).thenReturn(mockResponse500))) {
            exception = assertThrows(ServiceException.class, () -> {
                attachmentService.deleteBatchByIdList(0, idList);
            });
            assertEquals("An error occurred when communicating with the file server", exception.getMessage());
        }

        ResponseEntity<Void> mockResponse200 = new ResponseEntity<>(HttpStatus.OK);
        try (MockedConstruction<RestTemplate> ignored = mockConstruction(RestTemplate.class, (mock, context) -> when(mock.exchange(anyString(), any(), any(), eq(Void.class))).thenReturn(mockResponse200))) {
            attachmentService.deleteBatchByIdList(0, idList);
        }
    }
}