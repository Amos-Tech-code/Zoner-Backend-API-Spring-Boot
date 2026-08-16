# AGENTS.md

# Zoner Backend — AI Coding Agent Guide

## 1. Project Identity

Zoner Backend API is a **Spring Boot 4 backend** implemented using a deliberate mixture of **Kotlin and Java**.

The backend serves the Zoner Android application and is being developed as a modular social/business platform.

### Core stack

* Kotlin 2.2.x
* Java 21
* Spring Boot 4.x
* Spring Data JPA
* Hibernate
* PostgreSQL
* Flyway
* Spring Security
* JWT
* OAuth2 / Google authentication
* Spring Web MVC
* Spring WebSocket
* Spring Cache
* Spring Mail
* Firebase Admin SDK
* Cloudinary
* Gradle Kotlin DSL

### Primary package

```text
com.amos_tech_code.zoner
```

### Application entry point

```text
src/main/kotlin/com/amos_tech_code/zoner/ZonerBackendApiSpringBootApplication.kt
```

The application uses:

* `@SpringBootApplication`
* `@ConfigurationPropertiesScan`
* `@EnableAsync`

The application supports local `.env` configuration through `dotenv-java`.

Production environments such as Render provide environment variables directly and must not depend on a committed `.env` file.

---

# 2. High-Level Architecture

Zoner follows a **feature-oriented architecture**, not a layer-first global architecture.

Prefer:

```text
feature/
    controller/
    service/
        impl/
    repository/
    entity/
    dto/
        request/
        response/
    mapper/
    enums/
    event/
```

rather than organizing the entire application as:

```text
controllers/
services/
repositories/
entities/
```

### Typical request flow

```text
HTTP Request
     │
     ▼
Controller
     │
     ▼
Service Interface
     │
     ▼
ServiceImpl
     │
     ├── Repository
     ├── Domain validation
     ├── Entity changes
     └── Domain events
     │
     ▼
Database
```

Controllers should remain thin.

Business rules belong in services.

Persistence belongs in repositories.

---

# 3. Java + Kotlin Boundary

This project intentionally uses both Java and Kotlin.

Do **not** convert existing Java files to Kotlin or Kotlin files to Java without a concrete reason.

## Current broad ownership

### Java

The foundational modules were initially implemented in Java, especially:

```text
auth/
users/
business/
common/
media/
config/
```

Examples include:

```text
auth/service/impl/AuthServiceImpl.java
auth/controller/AuthController.java

business/entity/BusinessCategory.java
business/entity/BusinessProfile.java

users/entity/User.java

common/entity/BaseEntity.java

media/entity/Media.java

config/properties/AuthProperties.java
```

### Kotlin

The newer social functionality is being implemented in Kotlin.

Examples:

```text
social/follow/
social/post/
social/reaction/
```

The project is intentionally allowed to have Kotlin services consuming Java entities and repositories.

### Important Java/Kotlin interoperability rule

Java entities use Lombok:

```java
@Getter
@Setter
```

Kotlin accesses those generated getters/setters through Java interoperability.

Do not assume a Java private field is directly accessible from Kotlin.

For example:

```java
private UUID id;
```

must be accessed from Kotlin as:

```kotlin
entity.id
```

because Kotlin resolves the Java getter.

If Kotlin reports:

```text
Cannot access 'field id': it is private
```

inspect the Java entity and Lombok configuration before changing the entity unnecessarily.

---

# 4. BaseEntity

Most persistent domain entities extend:

```text
com.amos_tech_code.zoner.common.entity.BaseEntity
```

Current structure:

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    private Instant deletedAt;

    @Version
    private Long version;
}
```

Therefore, entities extending `BaseEntity` already have:

* UUID identity
* creation timestamp
* update timestamp
* soft-delete timestamp
* optimistic-locking version

Do **not** duplicate these fields in child entities.

Do not add fields such as:

```text
lastContentUpdatedAt
createdTimestamp
updatedTimestamp
version
```

unless there is a demonstrated domain-specific reason.

---

# 5. Soft Delete

Zoner uses soft deletion extensively.

Entities commonly contain:

```text
deletedAt
```

A deleted entity generally remains in the database but should not be treated as active application data.

Repositories should provide active-record queries where appropriate, for example:

```kotlin
findByIdAndDeletedAtIsNull(/*...*/)
```

When implementing new read operations, determine whether deleted records should be excluded.

Do not casually use:

```kotlin
findById(/*...*/)
```

when the domain requires an active entity.

---

# 6. Optimistic Locking

`BaseEntity` provides:

```java
@Version
private Long version;
```

Do not introduce custom version fields in individual entities.

The version field is intended to protect concurrent updates.

Be especially careful with counters such as:

```text
likesCount
commentsCount
bookmarksCount
viewsCount
repostsCount
```

These are persisted aggregate counters and are not merely calculated response fields.

---

# 7. Users

The core user entity is:

```text
users/entity/User.java
```

A `User` contains:

* email
* password hash
* username
* display name
* bio
* phone
* role
* registration stage
* account status
* visibility
* email verification state
* notification state
* two-factor state
* profile picture media reference
* business profile relationship
* authentication accounts
* email verification records

Current relationship:

```text
User
 │
 ├── AuthAccount
 │
 ├── EmailVerification
 │
 ├── ProfilePicture → Media
 │
 └── BusinessProfile
```

For now, Zoner strictly enforces:

```text
One User
   ↓
One BusinessProfile
```

Do not design multiple business profiles per user unless the product requirements explicitly change.

---

# 8. Business Domain

Business functionality is currently Java-based.

Primary entities:

```text
business/entity/BusinessCategory.java
business/entity/BusinessProfile.java
```

A `BusinessProfile` belongs to exactly one user:

```text
BusinessProfile
      │
      └── User
```

and belongs to one category:

```text
BusinessProfile
      │
      └── BusinessCategory
```

Current business profile media is referenced through `Media` entities rather than storing Cloudinary URLs directly.

Do not reintroduce fields such as:

```java
String logoUrl;
String coverPhotoUrl;
```

when the established model uses:

```java
Media logo;
Media cover;
```

The database stores media references by ID.

---

# 9. Media Architecture

Media is a centralized infrastructure module.

Primary entity:

```text
media/entity/Media.java
```

Media is the **single source of truth for uploaded files**.

Do not introduce separate tables such as:

```text
post_media
business_media
user_media
story_media
product_media
```

unless the architecture is explicitly redesigned.

Current media ownership model:

```text
MediaOwnerType
```

includes:

```text
USER
BUSINESS
POST
STORY
PRODUCT
MESSAGE
COMMENT
```

The media record contains:

```text
ownerType
ownerId
status
folder
resourceType
secureUrl
url
mimeType
format
bytes
width
height
duration
displayOrder
```

## Media ownership lifecycle

Temporary upload:

```text
ownerType = USER
ownerId = currentUserId
status = TEMPORARY
```

After the media is attached to a post:

```text
ownerType = POST
ownerId = postId
status = ACTIVE
```

The same conceptual lifecycle applies to other features.

Media ownership must not be null.

The client must provide `ownerType`, including for temporary uploads.

---

# 10. Cloudinary

Cloudinary is the external object/media storage provider.

The backend owns:

* upload validation
* upload policy
* media metadata
* ownership
* lifecycle
* deletion state

Cloudinary owns:

* physical file storage
* transformations
* CDN delivery

The application should not scatter Cloudinary-specific upload logic across business services.

Use the existing media infrastructure:

```text
MediaValidator
CloudinaryTransformationFactory
StorageService
MediaService
```

Business/social modules should work with media IDs rather than directly implementing Cloudinary uploads.

---

# 11. Media API Pattern

The generic media upload API is intentionally separated from business APIs.

The Android client should:

```text
1. Upload file through generic Media API
2. Receive MediaResponse
3. Use media ID in business/social API
```

Example:

```text
Android
   │
   ▼
POST /api/v1/media
   │
   ▼
Cloudinary
   │
   ▼
Media record
   │
   ▼
mediaId / secureUrl
```

Then:

```text
POST /api/v1/posts

{
    "caption": "...",
    "mediaIds": [...]
}
```

Business APIs should not receive multipart files unless there is an explicit architectural reason.

---

# 12. Authentication

Authentication is one of the foundational Java modules.

Current architecture includes:

```text
JWT access tokens
JWT refresh tokens
AuthAccount
EmailVerification
Google Sign-In
Account deactivation
Soft account deletion
```

Authentication services manage:

* registration
* email verification
* login
* refresh tokens
* logout/session lifecycle
* Google authentication
* account deactivation
* account deletion

When modifying authentication, preserve:

* User-Agent handling
* client IP extraction
* refresh-token/session behavior
* JWT claims
* account-status validation
* soft-delete behavior

Authentication requests may use:

```text
User-Agent
IP address
```

for security/session tracking.

Do not remove this information merely because it is not needed to authenticate the immediate request.

---

# 13. Google Authentication

Google Sign-In/Sign-Up is part of Phase 1 authentication.

Google authentication must integrate with the existing user/auth-account architecture rather than creating a parallel user system.

Do not create a second Google-specific user entity.

---

# 14. Account Lifecycle

The account lifecycle includes:

```text
Registration
     ↓
Email verification
     ↓
Active account
     ↓
Deactivation
     ↓
Reactivation where supported
     ↓
Soft deletion
```

Soft deletion should not physically destroy user records unless explicitly required.

Authentication must respect account status and deleted state.

---

# 15. REST API Versioning

REST endpoints use:

```text
/api/v1/
```

Example:

```text
/api/v1/auth/...
/api/v1/posts/...
/api/v1/business/...
/api/v1/media/...
```

Do not introduce unversioned endpoints such as:

```text
/api/posts
```

unless there is a deliberate API versioning decision.

---

# 16. Social Follow Module

Follow is implemented in Kotlin.

The follow model supports multiple target types:

```text
FollowTargetType.USER
FollowTargetType.BUSINESS
```

A user can follow:

```text
User → User
User → Business
```

The follow relationship is not represented as a generic media-like relationship.

Existing service capabilities include:

```text
follow()
unfollow()
isFollowing()
getFollowers()
getFollowing()
getStats()
```

The API should support both:

```text
followers
```

and:

```text
following
```

because they answer different queries.

Do not assume:

```text
GET /business/{id}/following
```

is automatically meaningful. Following is represented from the perspective of the user, while business follower listings are represented as followers of the business target.

---

# 17. Post Module

Posts are implemented in Kotlin.

Current entity:

```text
social/post/entity/Post.kt
```

A post currently belongs to a business:

```text
Post
 ↓
BusinessProfile
 ↓
User
```

For now:

```text
Only users with a BusinessProfile can create posts.
```

And:

```text
One User
   ↓
One BusinessProfile
```

Therefore, a post does not directly belong to an arbitrary user.

---

# 18. Post Status

Posts support lifecycle states through:

```text
PostStatus
```

At minimum, the architecture distinguishes:

```text
DRAFT
PUBLISHED
```

Do not treat every saved post as published content.

Android offline functionality is important.

The Android client is expected to support:

```text
Create draft offline
      ↓
Persist locally
      ↓
Synchronize later
      ↓
Backend creates/updates draft
      ↓
Publish when ready
```

Backend APIs must therefore preserve the distinction between drafts and published posts.

---

# 19. Post Visibility

Posts contain:

```text
PostVisibility
```

Do not bypass the visibility model when implementing feeds or post retrieval.

A post's:

* status
* visibility
* deletedAt
* business ownership

must be considered when determining whether it should be returned.

---

# 20. Post Editing

The post currently has:

```kotlin
var editedAt: Instant? = null
```

There is intentionally **no**:

```kotlin
var edited: Boolean
```

The presence of `editedAt` is sufficient to determine whether a post has been edited.

Do not add a redundant `edited` boolean.

---

# 21. Post Engagement Architecture

The Post entity contains cached counters:

```text
commentsCount
likesCount
bookmarksCount
viewsCount
repostsCount
```

These represent different forms of **post engagement**.

They are not all "reactions".

Architecture:

```text
Post Engagement
│
├── Reaction / Like
├── Comment
├── Bookmark
├── View
├── Repost
└── Share
```

Current product decision:

```text
Share does NOT have a count.
```

Sharing is an external action such as:

```text
WhatsApp
Telegram
Copy Link
Email
etc.
```

The backend must not pretend that an Android share-sheet invocation guarantees that the content was actually shared.

---

# 22. Reactions

The current reaction module supports:

```text
ReactionType.LIKE
```

Do not introduce additional reaction types unless requested.

The reaction entity represents:

```text
User
 +
Post
 +
ReactionType
```

A user may have only one reaction on a post.

The database must enforce the uniqueness invariant:

```text
(user_id, post_id)
```

Reaction flow:

```text
User taps Like
       ↓
ReactionService
       ↓
Create Reaction
       ↓
Post.likesCount++
       ↓
Publish ReactionCreatedEvent
```

Unlike:

```text
User taps Like again
       ↓
Delete Reaction
       ↓
Post.likesCount--
       ↓
Publish ReactionRemovedEvent
```

The reaction service should perform these core changes in a single transaction.

---

# 23. Engagement Counters

Counters are intentionally denormalized for efficient feed/post retrieval.

Do not calculate:

```text
likesCount
commentsCount
bookmarksCount
viewsCount
repostsCount
```

with a `COUNT(*)` query every time a post is returned.

The owning module should maintain its corresponding counter.

Example:

```text
Reaction module
    ↓
likesCount

Comment module
    ↓
commentsCount

Bookmark module
    ↓
bookmarksCount

View module
    ↓
viewsCount

Repost module
    ↓
repostsCount
```

Counters should never become negative.

---

# 24. Events

Domain events are used for secondary effects.

Examples:

```text
UserFollowedEvent
UserUnfollowedEvent

ReactionCreatedEvent
ReactionRemovedEvent
```

Core transactional state should not depend on an asynchronous listener.

For example:

```text
ReactionService
    ├── save Reaction
    ├── update likesCount
    └── publish event
```

is preferred over:

```text
ReactionService
    └── save Reaction

Event Listener
    └── update likesCount
```

The latter risks the primary state becoming temporarily inconsistent.

Events are appropriate for:

* notifications
* analytics
* feed-ranking signals
* other secondary processing

---

# 25. Repository Conventions

Repositories normally extend:

```text
JpaRepository<Entity, UUID>
```

Prefer Spring Data derived queries when they are sufficiently clear.

Examples:

```kotlin
findByIdAndDeletedAtIsNull(/*...*/)
existsBy(/*...*/)
findBy(/*...*/)
countBy(/*...*/)
```

Pagination uses:

```kotlin
Pageable
```

and:

```kotlin
Page<T>
```

where appropriate.

The post search endpoint is pageable.

Do not return huge unbounded collections from APIs that can grow indefinitely.

---

# 26. DTO Conventions

Do not expose JPA entities directly from REST controllers.

Use:

```text
dto/request
dto/response
```

Request DTOs represent client input.

Response DTOs represent API contracts.

Kotlin projects commonly use:

```kotlin
data class Example()
```

or records/data structures appropriate to the language.

Keep internal persistence details out of public DTOs.

---

# 27. Mapping

Use explicit mapping between entities and response DTOs.

Examples:

```text
ReactionMapper
MediaMapper
```

Do not allow controllers to construct complex response DTOs from JPA entities unless the mapping is genuinely trivial.

Avoid exposing lazy JPA relationships through serialization.

---

# 28. Security

Authenticated endpoints should use the existing Spring Security authentication mechanism.

Follow existing project conventions such as:

```kotlin
@AuthenticationPrincipal
```

or the project's established authenticated-user extraction mechanism.

Do not create a second authentication mechanism for a new feature.

Before implementing a secured endpoint, determine:

```text
Who is the authenticated user?
What resources do they own?
What roles/statuses are allowed?
Can the resource be deleted?
Can a user act on another user's resource?
```

---

# 29. Ownership Validation

Ownership must be explicitly validated.

For example, a user creating or modifying a post must be associated with the correct BusinessProfile.

Do not trust a client-provided:

```text
userId
businessId
ownerId
```

as proof of ownership.

Derive the authenticated user from the security context and validate the relationship server-side.

The same principle applies to media.

---

# 30. Media Ownership Validation

When a client uploads media before creating a post, the media may temporarily belong to:

```text
USER + userId
```

When creating the post:

```text
Validate media.ownerType == USER
Validate media.ownerId == authenticatedUserId
Validate media.status == TEMPORARY
```

Then transfer ownership:

```text
ownerType = POST
ownerId = postId
status = ACTIVE
```

The client must not be able to attach another user's temporary media to its post.

---

# 31. Android Integration

The backend is being developed incrementally alongside the Android client.

API design must therefore consider:

* offline operation
* drafts
* synchronization
* idempotency
* pagination
* cached engagement state
* minimal round trips

Avoid designing APIs that require many follow-up calls for every feed item.

For example, a future `PostResponse` should ideally provide current-user engagement state alongside aggregate counters:

```text
likesCount
commentsCount
bookmarksCount
viewsCount
repostsCount

likedByMe
bookmarkedByMe
repostedByMe
```

This avoids N+1 HTTP requests from Android.

---

# 32. Offline Drafts

Offline support is primarily implemented on Android, but backend APIs must support synchronization.

Do not assume:

```text
POST /posts
```

always means:

```text
publish immediately
```

Draft creation and publishing are separate concepts.

When implementing synchronization, consider:

* idempotency
* repeated requests
* local IDs/client-generated IDs where required
* conflict resolution
* optimistic locking
* media upload state
* draft status

Do not introduce synchronization infrastructure prematurely unless the current feature requires it.

---

# 33. Database

PostgreSQL is the primary database.

Flyway manages schema.

Migration files are under:

```text
src/main/resources/db/migration/
```

## Current development rule

The project is currently still in active development.

If a feature is being developed before the schema is considered frozen, existing SQL can be corrected directly rather than creating unnecessary migration files for every development iteration.

Once a migration has been applied to a shared/stable environment, **do not edit that migration**. Create a new versioned migration instead.

Never casually rewrite production-applied migrations.

---

# 34. Flyway Naming

Stable migrations should follow:

```text
V<number>__description.sql
```

Example:

```text
V13__create_post_reactions.sql
```

Do not invent random migration naming conventions.

Before changing a schema, inspect:

```text
src/main/resources/db/migration/
```

to understand the current migration state.

---

# 35. Entity/SQL Consistency

When modifying an entity, verify the corresponding SQL schema.

For example, if the entity contains:

```text
likesCount
```

the `posts` table must contain the corresponding column with the appropriate:

```text
NOT NULL
DEFAULT
```

behavior.

Do not assume Hibernate is responsible for production schema management.

The project uses Flyway.

---

# 36. Current Major Modules

The backend currently contains or is establishing these major domains:

```text
auth
business
common
config
media
users
social
```

Within social:

```text
social/follow
social/post
social/reaction
```

Planned/ongoing social modules include:

```text
comments
bookmarks
views
reposts
stories
products
messaging
notifications
```

These should remain feature-oriented.

---

# 37. Products, Stories, Messaging and Future Media

The media architecture is intentionally generic because Cloudinary will support media for:

```text
User avatars
Business logos
Business covers
Posts
Stories
Comments
Products
Messages
```

Do not create a new storage abstraction for every feature.

Use the existing:

```text
Media
StorageService
MediaValidator
CloudinaryTransformationFactory
UploadFolder
MediaOwnerType
MediaResourceType
```

infrastructure.

---

# 38. Cloudinary Rules

Cloudinary configuration belongs in environment/configuration properties.

Never commit:

```text
CLOUDINARY_API_KEY
CLOUDINARY_API_SECRET
```

or any other credentials.

Never hard-code secrets in:

```text
Java
Kotlin
application.properties
Dockerfile
```

Use environment variables.

---

# 39. Firebase

Firebase Admin is used for backend Firebase functionality.

Credentials must remain external to source control.

When modifying Firebase functionality, inspect existing configuration before introducing a second Firebase initialization mechanism.

---

# 40. Email

Email functionality uses Spring Mail.

Authentication-related email flows include email verification.

Preserve existing verification semantics such as:

* verification attempts
* expiration
* resend behavior
* account state

when modifying authentication.

---

# 41. Configuration Properties

The application uses:

```text
@ConfigurationPropertiesScan
```

and grouped configuration classes such as:

```text
config/properties/AuthProperties.java
```

Prefer typed configuration properties for grouped application configuration instead of scattering configuration access throughout services.

---

# 42. Exceptions

Use the project's existing exception hierarchy.

For example:

```text
ResourceNotFoundException
```

Do not introduce arbitrary exception types for every validation failure.

For invalid client requests, use the existing project-wide error handling conventions.

Before adding an exception handler, inspect the existing global exception handling.

---

# 43. Transactions

Service methods that perform multiple related database changes should normally be transactional.

Typical example:

```text
Create Reaction
+
Increment Post.likesCount
+
Publish Event
```

should execute inside one transaction.

Read-only operations should use:

```kotlin
@Transactional(readOnly = true)
```

where appropriate.

Do not put business transactions in controllers.

---

# 44. Lazy Loading

JPA relationships are commonly:

```text
FetchType.LAZY
```

Do not change them to eager loading merely to solve a serialization or mapping problem.

Instead:

* fetch the required data explicitly
* use appropriate repository queries
* map within a transaction
* use projections where justified

Changing everything to `EAGER` can create serious performance problems.

---

# 45. N+1 Queries

Be especially careful with social feeds.

Avoid code that retrieves:

```text
50 posts
```

and then causes:

```text
50 business queries
50 media queries
50 user queries
50 reaction queries
```

when a single optimized query or fetch strategy could provide the required data.

When implementing pageable feeds, inspect generated SQL when performance matters.

---

# 46. Pagination

Use Spring Data pagination:

```kotlin
Pageable
Page<T>
```

for potentially large collections:

```text
followers
following
posts
reactions
comments
messages
products
```

Do not load unbounded social data into memory.

---

# 47. API Design Principles

Prefer APIs that are:

* resource-oriented
* predictable
* versioned
* pageable where appropriate
* idempotent where synchronization requires it
* explicit about ownership
* optimized for Android consumption

Avoid unnecessary endpoints that force Android to make additional calls.

---

# 48. Current Post API Direction

The post module supports operations such as:

```text
create()
publish()
delete()
get(postId)
getBusinessPosts()
getDrafts()
```

The exact routes should be inspected from `PostController.kt` before modifying or extending them.

Do not duplicate existing post endpoints.

---

# 49. Current Reaction API Direction

The reaction module is designed around:

```text
POST /api/v1/posts/{postId}/reactions
```

for toggling a Like.

Additional read operations may include:

```text
GET /api/v1/posts/{postId}/reactions
GET /api/v1/posts/{postId}/reactions/me
```

The toggle response should provide enough information for Android to update its UI without an additional request, for example:

```json
{
  "liked": true,
  "likesCount": 27
}
```

---

# 50. Do Not Over-Engineer

Zoner is being built incrementally.

Do not introduce:

* unnecessary abstractions
* generic repositories for everything
* dozens of policy classes
* event infrastructure where direct service logic is sufficient
* microservices without a demonstrated need
* additional database tables merely because a concept could theoretically have one
* speculative caching
* speculative distributed systems

Prefer the smallest architecture that correctly supports the current requirements while preserving obvious extension points.

---

# 51. Do Not Duplicate Domain Concepts

Before creating a new entity/table/class, search the project.

Examples:

Do not create:

```text
PostMedia
```

if the established Media ownership model already supports:

```text
Media.ownerType = POST
Media.ownerId = postId
```

Do not create:

```text
UserProfilePicture
```

if:

```text
User.profilePicture → Media
```

already represents the relationship.

Do not create:

```text
BusinessLogo
BusinessCoverPhoto
```

tables when `BusinessProfile` already references the appropriate `Media`.

---

# 52. Code Style

### Kotlin

Prefer:

```kotlin
data class
class
object
```

where appropriate.

Use constructor injection.

Avoid field injection.

Use nullable types deliberately.

Do not use `!!` unless there is a strong invariant that has already been established.

### Java

Follow the existing Lombok style.

Typical annotations include:

```java
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
```

Use constructor injection through Lombok where the existing project convention supports it.

---

# 53. Repository Naming

Follow Spring Data conventions.

Examples:

```kotlin
existsByFollowerIdAndTargetTypeAndTargetId(/*...*/)
findByTargetTypeAndTargetId(/*...*/)
countByTargetTypeAndTargetId(/*...*/)
findByIdAndDeletedAtIsNull(/*...*/)
```

Prefer descriptive repository methods over opaque custom SQL when a derived query is sufficient.

Use `@Query` when a derived method would become excessively complex or inefficient.

---

# 54. Before Modifying Existing Code

An AI agent should first inspect:

1. The target feature.
2. The entity.
3. The repository.
4. The service interface.
5. The service implementation.
6. DTOs.
7. Controller.
8. Related migrations.
9. Existing exception handling.
10. Existing security/authentication conventions.

Do not immediately create new classes.

Search first.

---

# 55. Before Creating a New Entity

Confirm:

```text
Does an existing entity already represent this concept?

Can an existing Media relationship represent it?

Can an existing relationship/entity be extended?

Does this actually require persistence?

Is this a domain entity or merely a DTO/value object?
```

Avoid unnecessary tables.

---

# 56. Before Creating an Endpoint

Confirm:

```text
Does this endpoint already exist?

Who is allowed to call it?

What entity owns the resource?

Should the endpoint be pageable?

Should deleted records be excluded?

Does Android actually need a separate network call?

Can the required state be included in an existing response?
```

---

# 57. Build Commands

### Windows PowerShell

Build without tests:

```powershell
.\gradlew.bat clean build -x test
```

Full build:

```powershell
.\gradlew.bat clean build
```

Run tests:

```powershell
.\gradlew.bat test
```

Run locally:

```powershell
.\gradlew.bat bootRun
```

Build executable Spring Boot JAR:

```powershell
.\gradlew.bat clean bootJar
```

Run the generated JAR:

```powershell
java -jar build\libs\<generated-jar-name>.jar
```

Do not assume the JAR name contains `all`.

Inspect:

```text
build/libs/
```

if necessary.

---

# 58. Compilation Checks

Because Java and Kotlin coexist, always perform a full Gradle compilation after modifying cross-language entities.

At minimum:

```powershell
.\gradlew.bat clean build -x test
```

Do not rely only on IDE compilation.

A Java entity change can break Kotlin compilation and vice versa.

---

# 59. Docker / Render Deployment

The project is deployed as a Dockerized Spring Boot application.

The Dockerfile should:

```text
1. Build using JDK 21
2. Run Gradle bootJar
3. Use a Java 21 runtime image
4. Expose port 8080
5. Start the generated Spring Boot JAR
```

Example architecture:

```text
Gradle + JDK 21
       ↓
bootJar
       ↓
Java 21 JRE image
       ↓
Spring Boot
       ↓
Render
```

Spring Boot must use the Render-provided port:

```properties
server.port=${PORT:8080}
```

Never hard-code Render-specific secrets into Docker.

`.env` must not be copied into the Docker image.

---

# 60. Environment Variables

External secrets/configuration include, depending on the feature:

```text
DATABASE_URL / DB_URL
DB_USERNAME
DB_PASSWORD

JWT configuration

Google OAuth credentials

Cloudinary credentials

SMTP credentials

Firebase credentials
```

Use environment variables in deployed environments.

Do not commit:

```text
.env
service-account JSON
private keys
API secrets
database passwords
JWT signing secrets
Cloudinary secrets
```

---

# 61. Local Development Environment

Local development may use:

```text
.env
```

through `dotenv-java`.

The `.env` file is local configuration only.

Production should use Render environment variables.

The application should tolerate a missing `.env` in production.

---

# 62. Testing Strategy

When adding a feature, prioritize tests around:

### Service behavior

```text
successful operation
resource not found
unauthorized ownership
deleted resource
invalid state
duplicate operation
idempotent operation
counter changes
```

### Repository behavior

Test custom queries where their behavior is not obvious.

### Controller behavior

Verify:

```text
HTTP status
request validation
authentication
response shape
pagination
```

### Authentication

When touching auth, verify the flow:

```text
register
   ↓
email verification
   ↓
login
   ↓
access token
   ↓
refresh token
   ↓
logout/session invalidation
```

Also test Google authentication when changing that path.

---

# 63. Security Checklist

Before considering a secured feature complete, verify:

```text
[ ] Authentication required where appropriate
[ ] Authenticated user obtained from security context
[ ] Ownership validated
[ ] Deleted resources rejected
[ ] Account status checked
[ ] Client-provided owner IDs not blindly trusted
[ ] Sensitive data not returned
[ ] Secrets not committed
[ ] Pagination applied to unbounded resources
```

---

# 64. Performance Checklist

For social endpoints, verify:

```text
[ ] No obvious N+1 query
[ ] Pageable where collection can grow
[ ] Lazy relationships handled intentionally
[ ] Counters used instead of repeated COUNT queries
[ ] DTO projection/mapping considered
[ ] No unnecessary HTTP round trips
```

---

# 65. Data Integrity Checklist

For mutations:

```text
[ ] Transaction boundary is correct
[ ] Ownership validated
[ ] Soft deletion respected
[ ] Optimistic locking considered
[ ] Duplicate operation handled
[ ] Counter cannot become negative
[ ] Database constraints enforce important invariants
```

Application validation is not a substitute for database constraints.

For example, the reaction uniqueness rule must be enforced at the database level:

```text
UNIQUE(user_id, post_id)
```

---

# 66. Current Product Constraints

These are intentional constraints and should not be changed casually.

### Business

```text
One User → One BusinessProfile
```

### Posts

```text
Only users with a BusinessProfile can post.
```

### Media

```text
Media is the single source of truth for uploaded files.
```

### Media ownership

```text
ownerType is required.
```

Temporary media still has an owner:

```text
USER + userId
```

### Post media

Do not introduce `PostMedia` merely to associate media with posts.

Use:

```text
Media.ownerType = POST
Media.ownerId = postId
```

### Reactions

Currently:

```text
ReactionType = LIKE
```

### Post engagement

Current counters:

```text
commentsCount
likesCount
bookmarksCount
viewsCount
repostsCount
```

There is intentionally no:

```text
sharesCount
```

### Post editing

Use:

```text
editedAt
```

There is intentionally no:

```text
edited
```

---

# 67. Development Phase

The backend is being developed incrementally alongside the Android application.

The current strategy is:

```text
Backend feature
      ↓
Deploy/test API
      ↓
Android integration
      ↓
Validate real usage
      ↓
Continue backend feature
```

Do not attempt to complete the entire backend before Android integration.

API contracts should be stable enough for incremental client integration.

---

# 68. Current Feature Direction

The broad development sequence is:

```text
FOUNDATION
│
├── Auth
├── Users
├── Business
└── Media
        │
        ▼
SOCIAL
│
├── Follow
├── Posts
├── Reactions
├── Views
├── Bookmarks
├── Comments
├── Reposts
├── Feed
├── Stories
├── Products
├── Messaging
└── Notifications
```

The exact implementation order may change based on Android integration requirements.

---

# 69. Agent Workflow

When asked to implement a new feature:

### Step 1 — Understand

Inspect existing related code before writing anything.

### Step 2 — Identify ownership

Determine:

```text
Who owns the resource?
Who can mutate it?
Who can read it?
```

### Step 3 — Check existing infrastructure

Search for:

```text
existing entity
repository
service
DTO
mapper
exception
event
media support
security support
```

### Step 4 — Design minimally

Reuse existing architecture.

Do not introduce unnecessary abstractions.

### Step 5 — Implement

Follow the established:

```text
Controller
Service
ServiceImpl
Repository
Entity
DTO
Mapper
Event
```

pattern where appropriate.

### Step 6 — Database

Keep entity and SQL schema synchronized.

### Step 7 — Compile

Run:

```powershell
.\gradlew.bat clean build -x test
```

### Step 8 — Test behavior

Test the feature's:

```text
success
failure
authorization
ownership
edge cases
```

### Step 9 — Check API contract

Ensure the response is practical for Android.

### Step 10 — Avoid unrelated refactoring

Do not modify unrelated modules merely because they could be improved.

---

# 70. Agent Anti-Patterns

Do not:

* rewrite existing architecture without justification
* convert Java to Kotlin unnecessarily
* create duplicate entities
* create duplicate media tables
* bypass service layers from controllers
* expose JPA entities directly
* trust client ownership IDs
* store Cloudinary secrets in source
* introduce eager fetching as a quick fix
* ignore soft deletion
* ignore optimistic locking
* calculate every social counter dynamically
* create unbounded endpoints
* create migration files for every temporary development schema adjustment
* modify already-applied production Flyway migrations
* introduce microservices prematurely
* add speculative abstractions
* change established product constraints without discussion

---

# 71. Useful Search Locations

### Controllers

```text
src/main/**/controller/**/*Controller.*
```

### Services

```text
src/main/**/service/**
```

### Implementations

```text
src/main/**/service/impl/*ServiceImpl.*
```

### Repositories

```text
src/main/**/repository/**
```

### Entities

```text
src/main/**/entity/**
```

### DTOs

```text
src/main/**/dto/**
```

### Migrations

```text
src/main/resources/db/migration/**
```

### Configuration

```text
src/main/resources/application.properties
src/main/**/config/**
```

---

# 72. Recommended Reference Files

When implementing a new feature, use these as architectural references.

### Authentication

```text
auth/controller/AuthController.java
auth/service/impl/AuthServiceImpl.java
```

### Users

```text
users/entity/User.java
```

### Business

```text
business/entity/BusinessProfile.java
business/entity/BusinessCategory.java
```

### Media

```text
media/entity/Media.java
media/service/MediaService.java
media/service/impl/MediaServiceImpl.java
media/validation/MediaValidator.java
```

### Follow

```text
social/follow/service/impl/FollowServiceImpl.kt
```

### Posts

```text
social/post/entity/Post.kt
social/post/controller/PostController.kt
social/post/repository/PostRepository.kt
```

### Reactions

```text
social/reaction/
```

---

# 73. Final Principle

When modifying Zoner, preserve the following architectural direction:

```text
Simple domain model
        +
Strong database constraints
        +
Explicit ownership
        +
Feature-oriented modules
        +
Java/Kotlin interoperability
        +
Centralized media infrastructure
        +
Transactional core writes
        +
Events for secondary effects
        +
Android-friendly API contracts
        +
Incremental development
```

The goal is not to build the largest possible backend.

The goal is to build a backend whose **domain rules are explicit, whose data remains consistent, whose APIs are efficient for Android, and whose architecture can grow without repeatedly introducing new infrastructure for concepts the existing model already represents.**
