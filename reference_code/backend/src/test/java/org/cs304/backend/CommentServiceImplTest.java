package org.cs304.backend;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.cs304.backend.constant.constant_AttachmentType;
import org.cs304.backend.entity.*;
import org.cs304.backend.mapper.*;
import org.cs304.backend.service.IAttachmentService;
import org.cs304.backend.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.cs304.backend.constant.constant_AttachmentType.IMAGE;
import static org.cs304.backend.constant.constant_EntityType.COMMENT;
import static org.cs304.backend.constant.constant_User.ADMIN;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ReplyMapper replyMapper;
    @Mock
    private EventMapper eventMapper;

    @Mock
    private IAttachmentService attachmentService;

    @Mock
    private EntityAttachmentRelationMapper entityAttachmentRelationMapper;

    @Mock
    private UserBlogInteractionMapper userBlogInteractionMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(commentService, "baseMapper", commentMapper);

    }

    @Test
    @DisplayName("Should get all moments")
    public void shouldGetAllMoment() {
        Integer momentId = -1;
        Integer viewType = 1;
        String userId = "1";

        Comment comment = new Comment();
        comment.setId(1);
        comment.setPublisherId(userId);

        EntityAttachmentRelation entityAttachmentRelation = new EntityAttachmentRelation();
        entityAttachmentRelation.setAttachmentId(1);
        entityAttachmentRelation.setEntityId(1);
        entityAttachmentRelation.setEntityType(COMMENT);
        entityAttachmentRelation.setAttachmentType(IMAGE);


        when(entityAttachmentRelationMapper.selectList(any())).thenReturn(List.of(entityAttachmentRelation));
        when(commentMapper.selectList(any())).thenReturn(List.of(comment));

        List<JSONObject> result = commentService.getAllMoment(momentId, viewType, userId);

    }

    @Test
    @DisplayName("Should get moment by id")
    public void shouldGetMomentById() {
        Integer commentId = 1;

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setPublisherId("1");
        comment.setEventId(1);
        comment.setMediaType(Boolean.TRUE);

        User user = new User();
        user.setName("Test User");

        Event event = new Event();
        event.setName("Test Event");

        when(commentMapper.selectById(any())).thenReturn(comment);
        when(userMapper.selectById(any())).thenReturn(user);
        when(eventMapper.selectById(any())).thenReturn(event);

        JSONObject result = commentService.getMomentById(commentId);

        assertEquals(commentId, result.getInteger("id"));
        assertEquals(user.getName(), result.getString("userName"));
        assertEquals(event.getName(), result.getString("relatedEvent"));
    }

    @Test
    @DisplayName("Should delete moment by id")
    public void shouldDeleteMoment() {
        Integer momentId = 1;
        when(commentMapper.deleteById(1)).thenReturn(1);
        when(entityAttachmentRelationMapper.selectList(any())).thenReturn(List.of(new EntityAttachmentRelation()));
        when(userBlogInteractionMapper.delete(any())).thenReturn(1);
        when(replyMapper.delete(any())).thenReturn(1);
        when(entityAttachmentRelationMapper.delete(any())).thenReturn(1);
        assertDoesNotThrow(() -> commentService.deleteMoment(momentId));
    }

    @Test
    @DisplayName("Should start creating a moment")
    public void shouldCreateMomentStart() {
        String userId = "1";
        JSONObject comment = new JSONObject();
        comment.put("content", "Test content");
        comment.put("eventId", 1);
        comment.put("title", "Test title");
        comment.put("type", 1);
        comment.put("files", new JSONArray());
        JSONObject result = commentService.createMomentStart(comment, userId);

    }

    @Test
    @DisplayName("Should finish creating a moment")
    public void shouldCreateMomentFinish0() {
        Comment comment = new Comment();
        comment.setContent("Test content");
        comment.setPublisherId("1");
        comment.setEventId(1);
        comment.setType(COMMENT);
        comment.setPublishDate(null);
        comment.setTitle("Test title");
        comment.setUpVote(0);
        comment.setDownVote(0);
        comment.setMediaType(Boolean.TRUE);

        JSONObject requestData = JSONObject.parseObject(JSONObject.toJSONString(comment));
        requestData.put("serviceClassName", this.getClass().getName());
        requestData.put("serviceMethodName", "createMomentFinish");

        List<String> fileList = new ArrayList<>();
        fileList.add("Test file");

        requestData.put("files", fileList);

        List<String> fileDirList = new ArrayList<>();
        requestData.put("fileInfoList", fileDirList);

        when(commentMapper.insert(any())).thenReturn(1);

        commentService.createMomentFinish(requestData);

    }

    @Test
    @DisplayName("Should get all moments when momentId is -1 and viewType is 1")
    public void shouldGetAllMomentWhenMomentIdIsMinusOneAndViewTypeIsOne() {
        // Arrange
        Integer momentId = -1;
        Integer viewType = 1;
        String userId = "1";

        Comment comment = new Comment();
        comment.setId(1);
        comment.setPublisherId(userId);

        EntityAttachmentRelation entityAttachmentRelation = new EntityAttachmentRelation();
        entityAttachmentRelation.setAttachmentId(1);
        entityAttachmentRelation.setEntityId(1);
        entityAttachmentRelation.setEntityType(COMMENT);
        entityAttachmentRelation.setAttachmentType(IMAGE);

        when(commentMapper.selectList(any())).thenReturn(List.of(comment));
        when(entityAttachmentRelationMapper.selectList(any())).thenReturn(List.of(entityAttachmentRelation));

        // Act
        List<JSONObject> result = commentService.getAllMoment(momentId, viewType, userId);

    }

    @Test
    @DisplayName("Should get all moments when momentId is not -1 and viewType is 1")
    public void shouldGetAllMomentWhenMomentIdIsNotMinusOneAndViewTypeIsOne() {
        // Arrange
        Integer momentId = 1; // Not -1
        Integer viewType = 1;
        String userId = "1";

        Comment comment = new Comment();
        comment.setId(1);
        comment.setPublisherId(userId);

        EntityAttachmentRelation entityAttachmentRelation = new EntityAttachmentRelation();
        entityAttachmentRelation.setAttachmentId(1);
        entityAttachmentRelation.setEntityId(1);
        entityAttachmentRelation.setEntityType(COMMENT);
        entityAttachmentRelation.setAttachmentType(IMAGE);

        when(commentMapper.selectById(any())).thenReturn(comment);
        when(commentMapper.selectList(any())).thenReturn(List.of(comment));
        when(entityAttachmentRelationMapper.selectList(any())).thenReturn(List.of(entityAttachmentRelation));

        // Act
        List<JSONObject> result = commentService.getAllMoment(momentId, viewType, userId);

    }

    @Test
    @DisplayName("Should get all moments when momentId is -1 and viewType is not 1")
    public void shouldGetAllMomentWhenMomentIdIsMinusOneAndViewTypeIsNotOne() {
        // Arrange
        Integer momentId = -1;
        Integer viewType = 2; // Not 1
        String userId = "1";

        Comment comment = new Comment();
        comment.setId(1);
        comment.setPublisherId(userId);

        EntityAttachmentRelation entityAttachmentRelation = new EntityAttachmentRelation();
        entityAttachmentRelation.setAttachmentId(1);
        entityAttachmentRelation.setEntityId(1);
        entityAttachmentRelation.setEntityType(COMMENT);
        entityAttachmentRelation.setAttachmentType(IMAGE);

        when(commentMapper.selectList(any())).thenReturn(List.of(comment));
        when(entityAttachmentRelationMapper.selectList(any())).thenReturn(List.of(entityAttachmentRelation));

        // Act
        List<JSONObject> result = commentService.getAllMoment(momentId, viewType, userId);
    }

    @Test
    @DisplayName("Should get all moments when momentId is not -1 and viewType is not 1")
    public void shouldGetAllMomentWhenMomentIdIsNotMinusOneAndViewTypeIsNotOne() {
        // Arrange
        Integer momentId = 1; // Not -1
        Integer viewType = 2; // Not 1
        String userId = "1";

        Comment comment = new Comment();
        comment.setId(1);
        comment.setPublisherId(userId);

        EntityAttachmentRelation entityAttachmentRelation = new EntityAttachmentRelation();
        entityAttachmentRelation.setAttachmentId(1);
        entityAttachmentRelation.setEntityId(1);
        entityAttachmentRelation.setEntityType(COMMENT);
        entityAttachmentRelation.setAttachmentType(IMAGE);

        when(commentMapper.selectById(any())).thenReturn(comment);
        when(commentMapper.selectList(any())).thenReturn(List.of(comment));
        when(entityAttachmentRelationMapper.selectList(any())).thenReturn(List.of(entityAttachmentRelation));

        // Act
        List<JSONObject> result = commentService.getAllMoment(momentId, viewType, userId);
    }

    @Test
    @DisplayName("Should get event moments and return list of JSONObjects with correct data")
    public void shouldGetEventMoment() {
        // Arrange
        Integer eventId = 1;

        Comment comment = new Comment();
        comment.setId(1);
        comment.setPublisherId("1");
        comment.setEventId(eventId);

        EntityAttachmentRelation entityAttachmentRelation = new EntityAttachmentRelation();
        entityAttachmentRelation.setAttachmentId(1);
        entityAttachmentRelation.setEntityId(1);
        entityAttachmentRelation.setEntityType(COMMENT);
        entityAttachmentRelation.setAttachmentType(IMAGE);

        Attachment attachment = new Attachment();
        attachment.setId(1);
        attachment.setFilePath("path/to/file");

        when(commentMapper.selectList(any())).thenReturn(List.of(comment));
        when(entityAttachmentRelationMapper.selectList(any())).thenReturn(List.of(entityAttachmentRelation));
        when(attachmentService.getBatchByIds(ADMIN, List.of(1))).thenReturn(List.of(attachment));

        // Act
        List<JSONObject> result = commentService.getEventMoment(eventId);

        // Assert
        assertEquals(1, result.size());
        assertEquals(attachment.getFilePath(), result.get(0).getString("attachment"));
        assertEquals(comment.getId(), result.get(0).getInteger("comment_id"));
        assertEquals(comment.getPublisherId(), result.get(0).getString("publisher_id"));
    }

    @Test
    @DisplayName("Should finish creating a moment and correctly process each attachment")
    public void shouldCreateMomentFinish() {
        // Arrange
        Comment comment = new Comment();
        comment.setId(1);
        comment.setPublisherId("1");
        comment.setEventId(1);
        comment.setType(COMMENT);
        comment.setPublishDate(null);
        comment.setTitle("Test title");
        comment.setUpVote(0);
        comment.setDownVote(0);
        comment.setMediaType(Boolean.TRUE);

        Attachment attachment1 = new Attachment();
        attachment1.setId(1);
        attachment1.setFilePath("path/to/file1.mp4"); // Ends with "mp4"

        Attachment attachment2 = new Attachment();
        attachment2.setId(2);
        attachment2.setFilePath("path/to/file2.jpg"); // Does not end with "mp4"

        JSONObject requestData = new JSONObject();
        requestData.put("fileInfoList", List.of(attachment1, attachment2));
        requestData.putAll((JSONObject) JSONObject.from(comment));

        when(commentMapper.insert(any())).thenReturn(1);

        // Act
        JSONObject result = commentService.createMomentFinish(requestData);

        // Assert
        assertEquals(comment.getId(), result.getInteger("commentId"));
        verify(entityAttachmentRelationMapper, times(2)).insert(argThat(entityAttachmentRelation ->
                (entityAttachmentRelation.getEntityId().equals(comment.getId())) &&
                        (entityAttachmentRelation.getEntityType().equals(COMMENT)) &&
                        (entityAttachmentRelation.getAttachmentId().equals(attachment1.getId()) && entityAttachmentRelation.getAttachmentType().equals(constant_AttachmentType.VIDEO)) ||
                        (entityAttachmentRelation.getAttachmentId().equals(attachment2.getId()) && entityAttachmentRelation.getAttachmentType().equals(constant_AttachmentType.IMAGE))
        ));
    }

    @Test
    @DisplayName("Should get all moments and correctly process each attachment")
    public void shouldGetAllMoment1() {
        // Arrange
        Integer momentId = 1;
        Integer viewType = 1;
        String userId = "1";

        Comment comment = new Comment();
        comment.setId(1);
        comment.setPublisherId(userId);
        comment.setPublishDate(LocalDateTime.now());

        EntityAttachmentRelation entityAttachmentRelation = new EntityAttachmentRelation();
        entityAttachmentRelation.setAttachmentId(1);
        entityAttachmentRelation.setEntityId(1);
        entityAttachmentRelation.setEntityType(COMMENT);
        entityAttachmentRelation.setAttachmentType(IMAGE);

        Attachment attachment = new Attachment();
        attachment.setId(1);
        attachment.setFilePath("path/to/file");

        when(commentMapper.selectList(any())).thenReturn(List.of(comment));
        when(commentMapper.selectById(any())).thenReturn(comment);
        when(entityAttachmentRelationMapper.selectList(any())).thenReturn(List.of(entityAttachmentRelation));
        when(attachmentService.getBatchByIds(ADMIN, List.of(1))).thenReturn(List.of(attachment));
        when(entityAttachmentRelationMapper.selectList(any())).thenReturn(List.of(entityAttachmentRelation));
        when(attachmentService.getBatchByIds(ADMIN, List.of(1))).thenReturn(List.of(attachment));

        // Act
        List<JSONObject> result = commentService.getAllMoment(momentId, viewType, userId);

        // Assert
        assertEquals(1, result.size());
        assertEquals(attachment.getFilePath(), result.get(0).getString("attachment"));
        assertEquals(comment.getId(), result.get(0).getInteger("comment_id"));
        assertEquals(comment.getPublisherId(), result.get(0).getString("publisher_id"));
    }
}