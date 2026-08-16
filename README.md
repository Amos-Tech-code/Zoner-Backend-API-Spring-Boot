# Zoner Backend API

**Zoner** is a modular social-commerce backend built with **Spring Boot**, using **Kotlin and Java** in a mixed-language codebase.

The platform is designed around the idea of connecting users, businesses, social content, products, and messaging into a single ecosystem.

The backend provides REST APIs for authentication, user accounts, business profiles, media management, social interactions, posts, reactions, and other platform capabilities that are being developed incrementally alongside the Zoner Android application.

---

## Table of Contents

* [Overview](#overview)
* [Architecture](#architecture)
* [Technology Stack](#technology-stack)
* [Project Structure](#project-structure)
* [Core Modules](#core-modules)
* [Authentication](#authentication)
* [Media Management](#media-management)
* [Business Profiles](#business-profiles)
* [Social Features](#social-features)
* [Post System](#post-system)
* [Data and Persistence](#data-and-persistence)
* [API Design](#api-design)
* [Configuration](#configuration)
* [Running Locally](#running-locally)
* [Building the Project](#building-the-project)
* [Database Migrations](#database-migrations)
* [Docker](#docker)
* [Development Approach](#development-approach)
* [Current Development Status](#current-development-status)
* [Roadmap](#roadmap)
* [Project Principles](#project-principles)

---

## Overview

Zoner is being developed as a **social-commerce platform** where businesses can establish a presence, publish content, interact with users, and eventually expose products and other commerce-related functionality.

The backend is designed to support:

* User accounts
* Email/password authentication
* Google authentication
* JWT-based authentication
* Refresh-token sessions
* Account deactivation
* Soft account deletion
* User profiles
* Business profiles
* Business categories
* Cloud-based media management
* Social following
* Posts
* Post drafts
* Post publishing
* Post reactions
* Bookmarks
* Comments
* Views
* Reposts
* Messaging
* Products
* Stories
* Notifications
* Offline-first Android workflows

The system is being implemented incrementally, with the backend and Android client developed in parallel.

---

# Architecture

Zoner follows a **feature-oriented architecture** rather than organizing the entire application strictly by technical layer.

A typical feature follows:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
JPA Entity
    ↓
PostgreSQL
```

For example:

```text
PostController
      ↓
PostService
      ↓
PostServiceImpl
      ↓
PostRepository
      ↓
Post
      ↓
PostgreSQL
```

The project also separates infrastructure concerns such as:

```text
Authentication
Media / Storage
Security
Configuration
Database migrations
External integrations
```

This structure allows individual features to evolve without turning the application into one large collection of unrelated controllers and services.

---

# Technology Stack

## Backend

| Technology        | Purpose                                  |
| ----------------- | ---------------------------------------- |
| Kotlin            | Primary language for newer modules       |
| Java              | Existing and infrastructure/auth modules |
| Spring Boot       | Application framework                    |
| Spring MVC        | REST API                                 |
| Spring Security   | Authentication and authorization         |
| Spring Data JPA   | Persistence                              |
| Hibernate         | ORM                                      |
| PostgreSQL        | Relational database                      |
| Flyway            | Database migrations                      |
| Gradle Kotlin DSL | Build system                             |

## Authentication & Security

| Technology         | Purpose                              |
| ------------------ | ------------------------------------ |
| JWT                | Access-token authentication          |
| Refresh Tokens     | Session renewal                      |
| Spring Security    | Request authentication/authorization |
| Google APIs        | Google authentication                |
| Firebase Admin SDK | Firebase integration                 |

## Infrastructure & Integrations

| Technology             | Purpose                    |
| ---------------------- | -------------------------- |
| Cloudinary             | Media storage and delivery |
| JavaMail / Spring Mail | Email functionality        |
| WebSocket              | Real-time communication    |
| Spring Cache           | Caching                    |
| Micrometer / Actuator  | Application monitoring     |
| Docker                 | Containerized deployment   |

---

# Project Structure

The main source tree is organized by feature:

```text
src/
└── main/
    ├── java/
    │   └── com/amos_tech_code/zoner/
    │       ├── auth/
    │       ├── business/
    │       ├── config/
    │       ├── media/
    │       ├── users/
    │       └── ...
    │
    ├── kotlin/
    │   └── com/amos_tech_code/zoner/
    │       ├── social/
    │       │   ├── follow/
    │       │   ├── post/
    │       │   └── ...
    │       └── ZonerBackendApiSpringBootApplication.kt
    │
    └── resources/
        ├── application.properties
        └── db/
            └── migration/
```

The project intentionally supports **Java and Kotlin together**.

Existing Java functionality is not converted simply for the sake of language consistency. New modules can be implemented in Kotlin where appropriate.

---

# Core Modules

## Authentication

The authentication system supports:

```text
Registration
    ↓
Email Verification
    ↓
Authentication
    ↓
JWT Access Token
    +
Refresh Token
```

Authentication functionality includes:

* Email/password registration
* Email verification
* Login
* Google Sign-In / Sign-Up
* JWT access tokens
* Refresh tokens
* Session management
* Account deactivation
* Soft account deletion
* Security metadata such as User-Agent and client IP

Authentication-related functionality is primarily located under:

```text
auth/
users/
```

---

# User Management

Users contain platform-level account information such as:

* Email
* Username
* Display name
* Bio
* Phone
* Profile visibility
* Account status
* Registration stage
* Role
* Email verification status
* Notification preferences
* Two-factor authentication state

User profile images are managed through the centralized media system rather than storing image metadata directly in the user table.

Conceptually:

```text
User
  │
  └── profilePicture → Media
```

---

# Business Profiles

Zoner currently follows a strict relationship:

```text
One User
    ↓
One BusinessProfile
```

A user with a business profile can represent a business on the platform.

Business profiles contain information such as:

* Business name
* Business category
* Description
* Contact information
* Website
* Location
* Verification status
* Featured status
* Messaging preferences
* Business logo
* Business cover photo

Media such as logos and cover photos are linked through the centralized media system.

```text
BusinessProfile
    ├── logo → Media
    └── coverPhoto → Media
```

---

# Media Management

Media is handled through a centralized media subsystem backed by **Cloudinary**.

The `Media` entity is the platform's single source of truth for uploaded files.

Supported resource types currently include:

```text
IMAGE
VIDEO
RAW
```

Media ownership is represented through:

```text
MediaOwnerType
```

including:

```text
USER
BUSINESS
POST
STORY
PRODUCT
MESSAGE
COMMENT
```

The media model stores information such as:

* Cloudinary public ID
* URL
* Secure URL
* Resource type
* MIME type
* Format
* File size
* Width
* Height
* Duration
* Upload folder
* Owner type
* Owner ID
* Display order
* Status
* Creation timestamp
* Deletion timestamp

### Media lifecycle

Temporary uploads can initially belong to a user:

```text
Upload
    ↓
ownerType = USER
ownerId = userId
status = TEMPORARY
```

When the media becomes associated with a permanent resource, ownership can be transferred:

```text
Create Post
    ↓
Validate media belongs to current user
    ↓
ownerType = POST
ownerId = postId
status = ACTIVE
```

This allows the same centralized media infrastructure to support posts, stories, products, messages, comments, profiles and businesses without creating separate storage systems for each feature.

---

# Social Features

The social subsystem is being implemented incrementally.

Current/planned social functionality includes:

* Following users
* Following businesses
* Followers
* Following lists
* Follow statistics
* Posts
* Reactions
* Bookmarks
* Comments
* Views
* Reposts
* Sharing

The follow system supports multiple target types:

```text
USER
BUSINESS
```

Conceptually:

```text
User
 │
 ├── follows User
 │
 └── follows Business
```

The API also supports retrieving the resources a user is following.

---

# Post System

Posts belong to a business.

The current Zoner rule is:

```text
User
   ↓
BusinessProfile
   ↓
Post
```

A post therefore references a `BusinessProfile` rather than directly storing the author as a separate post owner.

The current post model supports:

* Caption
* Visibility
* Draft status
* Publishing
* Comments enabled/disabled
* Sharing enabled/disabled
* Edited timestamp
* Published timestamp
* Comment count
* Like count
* Bookmark count
* View count
* Repost count

Example lifecycle:

```text
DRAFT
  ↓
PUBLISHED
  ↓
DELETED
```

Posts can be created as drafts before being published.

---

# Post Media

Zoner intentionally does **not** use a separate `post_media` table.

The centralized `media` table is used instead.

For example:

```text
Temporary upload

Media
├── ownerType = USER
├── ownerId = userId
└── status = TEMPORARY
```

After the post is created:

```text
Media
├── ownerType = POST
├── ownerId = postId
└── status = ACTIVE
```

This keeps media ownership centralized and avoids duplicating media relationships across multiple feature-specific join tables.

The `displayOrder` field allows posts to contain multiple media items while preserving their ordering.

---

# Post Reactions

The initial reaction system is intentionally small.

Currently:

```text
LIKE
```

is the supported reaction type.

The post itself maintains denormalized counters such as:

```text
likesCount
commentsCount
bookmarksCount
viewsCount
repostsCount
```

Not every interaction is considered a "reaction".

For example:

* Like → reaction
* Comment → interaction
* Bookmark → interaction
* View → engagement metric
* Repost → separate social action
* Share → action without a persistent share count

This distinction allows the domain model to evolve without forcing every form of engagement into the same abstraction.

---

# Offline-First Android Support

The backend is being developed alongside a Zoner Android application.

The Android client is intended to support offline workflows, particularly for drafts.

For example:

```text
Android Client
      ↓
Create Draft Locally
      ↓
Store Offline
      ↓
Connectivity Available
      ↓
Upload Media
      ↓
Create / Sync Post
      ↓
Publish
```

The backend therefore distinguishes between concepts such as:

* temporary media
* draft posts
* published posts

This is important because an Android client may create and retain content locally before communicating with the backend.

---

# Data and Persistence

Zoner uses:

* PostgreSQL
* Spring Data JPA
* Hibernate
* Flyway

Most persistent entities extend:

```java
BaseEntity
```

which provides common fields such as:

```text
id
createdAt
updatedAt
deletedAt
version
```

This provides:

* UUID-based identifiers
* auditing timestamps
* soft deletion
* optimistic locking

The project uses soft deletion in several important domains rather than immediately physically removing records.

---

# Database Migrations

Database schema changes are managed through Flyway.

Migration files are located under:

```text
src/main/resources/db/migration/
```

Example:

```text
V1__...
V2__...
V3__...
```

During active development, schema changes may be made directly to the existing development SQL definitions when appropriate.

Once a migration has been released/applied as part of the stable migration history, future schema changes should be introduced through a new migration rather than modifying an already-applied migration.

---

# API Design

REST APIs use the versioned base path:

```text
/api/v1
```

Feature endpoints follow the resource-oriented structure.

Examples:

```text
/api/v1/auth/...
/api/v1/users/...
/api/v1/business/...
/api/v1/media/...
/api/v1/posts/...
/api/v1/follows/...
```

Secured endpoints generally obtain the authenticated user through Spring Security's authentication context / `@AuthenticationPrincipal`.

Pagination is implemented using Spring Data's:

```text
Pageable
```

where appropriate.

---

# Configuration

Application configuration is primarily located in:

```text
src/main/resources/application.properties
```

Configuration includes areas such as:

```text
Database
Flyway
JWT
Mail
Firebase
Cloudinary
Multipart uploads
Spring
Actuator
```

The application also loads environment variables from `.env` during local development.

The application entry point is:

```text
src/main/kotlin/com/amos_tech_code/zoner/ZonerBackendApiSpringBootApplication.kt
```

It enables:

* Spring Boot
* Configuration Properties scanning
* Async processing
* dotenv-based local configuration

### Important

Secrets must **not** be committed to Git.

Use environment variables or a local `.env` file for development.

---

# Running Locally

## Requirements

Recommended development environment:

* JDK 21
* PostgreSQL
* Gradle Wrapper
* Git

The project uses the Gradle Wrapper, so installing Gradle globally is not required.

### Clone

```bash
git clone <repository-url>
cd Zoner-Backened-API
```

### Configure environment

Create a local `.env` file containing the required development configuration.

Do not commit this file.

### Run

Windows:

```powershell
.\gradlew.bat bootRun
```

The application will start using the configured PostgreSQL database and external services.

---

# Building

To compile and package the project:

```powershell
.\gradlew.bat clean build
```

To build without tests:

```powershell
.\gradlew.bat clean build -x test
```

To run tests:

```powershell
.\gradlew.bat test
```

To generate the Spring Boot JAR:

```powershell
.\gradlew.bat bootJar
```

---

# Docker

The backend can be containerized for deployment.

The intended deployment architecture is:

```text
Android Application
        │
        │ HTTPS
        ▼
   Zoner Backend
   Spring Boot
        │
        ├── PostgreSQL
        │
        ├── Cloudinary
        │
        ├── Firebase
        │
        └── SMTP
```

The containerized backend can be deployed to platforms such as Render.

Environment-specific configuration should be supplied through the hosting platform rather than hardcoded into the Docker image.

---

# Development Approach

Zoner is being developed incrementally.

The implementation strategy is to complete a coherent backend capability before integrating it into the Android application.

For example:

```text
Backend feature
      ↓
API
      ↓
Backend testing
      ↓
Android integration
      ↓
Offline/client behavior
      ↓
Next backend feature
```

This approach allows the Android application to consume stable APIs while the backend continues to evolve.

---

# Current Development Status

The project is currently under active development.

### Authentication

Implemented:

* Email/password authentication
* Email verification
* JWT authentication
* Refresh-token flow
* Google Sign-In / Sign-Up
* Account deactivation
* Soft account deletion

### Media

Implemented:

* Generic media upload API
* Cloudinary integration
* Media validation
* Media metadata persistence
* Temporary media
* Media ownership
* User profile picture linking
* Business logo linking
* Business cover photo linking

### Business

Implemented foundation:

* Business categories
* Business profiles
* One-user-to-one-business-profile relationship
* Business media associations

### Social

Implemented / under development:

* User following
* Business following
* Followers
* Following lists
* Follow statistics
* Post foundation
* Post drafts
* Post publishing
* Post deletion
* Post retrieval
* Post reactions

### In progress

The broader social-commerce platform is still being built.

Planned modules include:

* Comments
* Bookmarks
* Views
* Reposts
* Stories
* Products
* Messaging
* Notifications
* Additional media workflows

---

# Roadmap

The roadmap is intentionally iterative.

## Phase 1 — Authentication & Accounts

```text
✓ Registration
✓ Email verification
✓ Login
✓ JWT
✓ Refresh tokens
✓ Google authentication
✓ Account deactivation
✓ Soft account deletion
```

## Phase 2 — Media & Profiles

```text
✓ Generic media upload
✓ Cloudinary storage
✓ User profile media
✓ Business logo
✓ Business cover
✓ Media ownership
✓ Temporary media
```

## Phase 3 — Social Foundation

```text
✓ Follow users
✓ Follow businesses
✓ Followers
✓ Following
✓ Follow statistics
✓ Post foundation
✓ Draft posts
✓ Publishing
✓ Post retrieval
```

## Phase 4 — Post Engagement

```text
✓ Like foundation
→ Comments
→ Bookmarks
→ Views
→ Reposts
→ Sharing
```

## Phase 5 — Content

```text
→ Stories
→ Additional media workflows
→ Content discovery
```

## Phase 6 — Commerce

```text
→ Products
→ Product media
→ Business commerce workflows
```

## Phase 7 — Communication

```text
→ Messaging
→ WebSocket communication
→ Notifications
```

The roadmap may change as the Android client and backend requirements evolve.

---

# Project Principles

## 1. Feature-oriented design

Code should remain organized around business capabilities rather than becoming a collection of unrelated technical layers.

## 2. Single source of truth

Where a centralized domain model already exists, avoid unnecessary duplicate representations.

For example, media is represented by the centralized `Media` entity rather than creating separate storage tables for every feature.

## 3. Explicit ownership

Resources should have clear ownership.

For media:

```text
ownerType
ownerId
```

determine which domain resource currently owns the media.

## 4. Soft deletion

Important entities use soft deletion where appropriate.

```text
deletedAt != null
```

means the resource should generally be considered deleted without immediately destroying the database record.

## 5. Kotlin and Java coexistence

Zoner intentionally supports a mixed Kotlin/Java codebase.

Existing Java implementations should not be rewritten simply to make the project uniform.

## 6. API stability

Backend APIs are consumed by the Android application.

Changes to request/response contracts should therefore be deliberate and coordinated with client-side changes.

## 7. Offline-aware design

The Android application is designed to support offline workflows.

Backend APIs should therefore account for:

* retries
* idempotency
* draft synchronization
* temporary resources
* eventual synchronization

where applicable.

---

# Related Application

The Zoner backend is being developed alongside the Zoner Android application.

The Android client communicates with the backend through the versioned REST API:

```text
/api/v1
```

The two projects are developed in parallel so that backend capabilities can be integrated into the Android application incrementally.

---

# Status

**Zoner Backend API is an active development project.**

The architecture and domain model are still evolving as new social-commerce capabilities are implemented.

The current priority is building a reliable backend foundation before expanding into the full social, commerce, messaging, and content ecosystem.

---

## License

This project is currently under development.

License information will be added when the project is ready for public distribution.
