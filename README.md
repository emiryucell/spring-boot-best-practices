# Course Portal - Spring Boot Best Practices - Reference Project Template

A Spring Boot reference project showcasing modern Java development best practices, architectural patterns, and implementation techniques. This project serves as a **cheatsheet, guide or template** for creating new Spring Boot applications, demonstrating how various technologies and patterns work together.

## 🎯 Purpose

This project is designed as a **personal reference and learning tool** to:
- Demonstrate Spring Boot architectural patterns
- Showcase best practices for enterprise-grade applications  
- Provide code examples for common implementation scenarios
- Serve as a quick reference when starting new Spring Boot projects
- Illustrate proper usage of Spring ecosystem technologies

## 🛠️ Technology Stack

- **Java 21** - LTS version
- **Spring Boot 3.4.4** - Main framework
- **Spring Data JPA** - Database access layer
- **Spring Web** - REST API development
- **PostgreSQL** - Primary database
- **MapStruct 1.5.5** - Object mapping
- **Lombok** - Boilerplate code reduction
- **JUnit 5 and Mockito** - Unit testing
- **Maven** - Dependency management and build tool

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/emiryucel/courseportal/
│   │   ├── controller/          # REST controllers
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── exception/           # Custom exceptions and global handler
│   │   ├── mapper/              # MapStruct mappers
│   │   ├── model/               # JPA entities
│   │   ├── repository/          # Data access layer
│   │   ├── service/             # Business logic layer
│   │   └── CoursePortalApplication.java
│   └── resources/
│       ├── application.properties
│       └── logback-spring.xml
└── test/                        # Unit tests
│   └── java/com/emiryucel/courseportal/
│         ├── controller/          
│         └── service/               
```

## 🏗️ Architecture & Best Practices Demonstrated

### **Layered Architecture Pattern**
- **Controller Layer**: REST endpoints with proper HTTP semantics
- **Service Layer**: Business logic implementation with interface segregation
- **Repository Layer**: Data access abstraction using Spring Data JPA
- **DTO Layer**: Clean separation between API contracts and domain models

### **Dependency Injection Patterns**
- **Constructor Injection**: Preferred approach for mandatory dependencies
- **Field Injection**: Avoided in favor of constructor injection
- **Interface-Based Design**: Services defined as interfaces for better testability

### **Data Management Best Practices**
- **Server-Side Pagination**: Efficient handling of large datasets
- **Entity Relationships**: Proper JPA annotations and lazy loading
- **Database Validation**: Multi-layer validation (Bean Validation + Database constraints)
- **Transaction Management**: Declarative transaction handling

### **Error Handling & Validation**
- **Global Exception Handler**: Centralized error handling strategy
- **Custom Exceptions**: Domain-specific exception types
- **Bean Validation**: Comprehensive input validation with custom messages
- **Structured Error Responses**: Consistent API error format

### **Testing Strategy**
- **Unit Testing**: Controller and service layer testing
- **Mock-based Testing**: Proper isolation of components
- **Test Data Management**: Clean test setup and teardown

## 💡 Key Implementation Examples & Patterns

### **1. Constructor Dependency Injection**
**Why**: Ensures immutability, makes dependencies explicit, and enables better testing
```java
@RestController
@RequiredArgsConstructor  // Lombok generates constructor
public class CourseController {
    private final CourseService courseService;  // Final field ensures immutability
}
```

### **2. Server-Side Pagination**
**Why**: Efficient handling of large datasets, reduces memory usage and network traffic
```java
@GetMapping("/paginated")
public ResponseEntity<Page<CourseResponseDTO>> getAllCoursesPaginated(Pageable pageable) {
    Page<CourseResponseDTO> courses = courseService.getAllCourses(pageable);
    return ResponseEntity.ok(courses);
}

// Usage: GET /api/course/paginated?page=0&size=10&sort=title,asc
```

### **3. JPA Entity Relationships & Lazy Loading**
**Why**: Prevents N+1 queries, manages bidirectional relationships properly
```java
@Entity
public class Lecturer {
    @OneToMany(mappedBy = "lecturer", cascade = CascadeType.ALL)
    private Set<Course> courses = new HashSet<>();
    
    // Helper methods for bidirectional relationship management
    public void addCourse(Course course) {
        courses.add(course);
        course.setLecturer(this);
    }
}

@Entity  
public class Course {
    @ManyToOne(fetch = FetchType.LAZY)  // Lazy loading for performance
    @JoinColumn(name = "lecturer_id")
    private Lecturer lecturer;
}
```

### **4. Bean Validation with Custom Messages**
**Why**: Centralized validation logic, consistent error messages, early validation
```java
@Data
public class CourseDTO {
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @DecimalMax(value = "9999.99", message = "Price must be less than 10000")
    private Double price;
}
```

### **5. Global Exception Handling**
**Why**: Centralized error handling, consistent API responses, separation of concerns
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
```

### **6. Service Layer Interface Pattern**
**Why**: Enables easier testing, loose coupling, and multiple implementations increasing readability for other developers
```java
public interface CourseService {
    CourseResponseDTO createCourse(CourseDTO courseDTO);
    Page<CourseResponseDTO> getAllCourses(Pageable pageable);
}

@Service
@Transactional
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
}
```

### **7. DTO Pattern for API Contracts**
**Why**: Separates internal domain models from API contracts, enables API versioning
```java
// Request DTO - What client sends
public class CourseDTO {
    private String title;
    private String description;
    private Double price;
}

// Response DTO - What API returns  
public class CourseResponseDTO {

    private String title;
    private String description;
    private Double price;
    private LocalDateTime createdAt;
    private LecturerResponseDTO lecturer;
}
```

### **8. MapStruct for Object Mapping**
**Why**: Compile-time mapping generation, type-safe, high performance
```java
@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseResponseDTO toResponseDTO(Course course);
    Course toEntity(CourseDTO courseDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Course toEntityForUpdate(CourseDTO courseDTO);
}
```

## 🧪 Testing Best Practices

### **Unit Testing with @WebMvcTest**
**Why**: Fast, focused testing of web layer with mocked dependencies
```java
@WebMvcTest(CourseController.class)
class CourseControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private CourseService courseService;
    
    @Test
    void givenValidCourseDTO_whenCreateCourse_thenReturnCreatedCourse() throws Exception {
        when(courseService.createCourse(any(CourseDTO.class))).thenReturn(courseResponseDTO);
        
        mockMvc.perform(post("/course")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Java Programming"));
    }
}
```

### **Service Layer Testing**
**Why**: Tests business logic in isolation with mocked repositories
```java
@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {
    
    @Mock
    private CourseRepository courseRepository;
    
    @Mock
    private CourseMapper courseMapper;
    
    @InjectMocks
    private CourseServiceImpl courseService;
}
```

## 🔧 Database Best Practices

### **Entity Lifecycle Management**
**Why**: Automatic timestamp management, consistent data tracking
```java
@Entity
public class Course {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

### **UUID Primary Keys**
**Why**: Better for distributed systems, no collision risk, database-agnostic
```java
@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
}
```

### **Repository Pattern with Spring Data JPA**
**Why**: Abstraction over data access, automatic query generation
```java
@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, String> {
    Optional<Lecturer> findByEmail(String email);
} 
```

## 📊 Logging & Monitoring

### **Structured Logging with SLF4J**
**Why**: Consistent log format, configurable levels, performance
```java
@Slf4j
@RestController
public class CourseController {
    
    @PostMapping
    public ResponseEntity<CourseResponseDTO> createCourse(@Valid @RequestBody CourseDTO courseDTO) {
        log.info("Creating new course with title: {}", courseDTO.getTitle());
        CourseResponseDTO createdCourse = courseService.createCourse(courseDTO);
        log.info("Course created successfully with ID: {}", createdCourse.getId());
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }
}
```


