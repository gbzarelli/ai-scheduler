# AI Scheduler with MCP Server and Client

This project consists of an AI-driven scheduling platform using Spring Boot, designed to manage calendar events for multiple users. It incorporates Google Calendar API for event management and OpenAI's GPT for AI-driven interactions. The project includes two main components: the MCP Server and MCP Client.

## Features

- **MCP Server**: Manages calendar operations such as creating, listing, and deleting both calendars and events using the Google Calendar API.
- **MCP Client**: Provides AI capabilities to handle user queries for calendar scheduling through OpenAI's models.
- **Intelligent Event Management**: Automatically create, search, and manage events based on user's natural language input.
- **Ensures No Overlapping Events**: Prevents scheduling conflicts and avoids creating events in the past.

## Project Structure

- `gcalendar-mcp-server`: Implements the server-side logic with Google Calendar integration.
    - **DemoApplication**: Main entry point.
    - **ScheduleService**: Service containing methods for calendar operations.

- `gcalendar-mcp-client`: Houses the AI components leveraging OpenAI models.
    - **DemoApplication**: Client application entry point.
    - **ChatController**: Handles API requests for chat interactions.
    - **AiConfig**: Configures AI memory and chat client.

## Getting Started

### Prerequisites

- **Java 21**
- **Maven**
- **Docker and Docker Compose** (recommended for PostgreSQL setup)
- **PostgreSQL** (alternative to Docker setup)
- **Google Calendar API credentials**
- **OpenAI API key** (or OpenRouter API key for alternative models)

### Installation

#### Environment Setup

Before running either component, set up the required environment variables:

```bash
# Set your OpenAI API key (or OpenRouter API key)
export OPENAI_API_KEY="your-api-key-here"
```

#### MCP Server

1. Navigate to `gcalendar-mcp-server`:
   ```bash
   cd gcalendar-mcp-server
   ```
2. Set up Google Calendar credentials:
   
   **Creating Service Account Credentials:**
   - Go to the [Google Cloud Console](https://console.cloud.google.com/)
   - Create a new project or select an existing one
   - Enable the Google Calendar API:
     - Navigate to "APIs & Services" > "Library"
     - Search for "Google Calendar API" and enable it
   - Create a Service Account:
     - Go to "APIs & Services" > "Credentials"
     - Click "Create Credentials" > "Service Account"
     - Fill in the service account details and click "Create"
     - Skip granting roles (optional) and click "Continue"
     - Click "Done"
   - Generate and download the JSON key:
     - Click on the created service account
     - Go to the "Keys" tab
     - Click "Add Key" > "Create new key"
     - Select "JSON" format and click "Create"
     - Save the downloaded file as `credentials.json` in `src/main/resources/`
   - Share your calendar with the service account:
     - Copy the service account email from the credentials file
     - In Google Calendar, share your calendar with this email address
     - Give it "Make changes and manage sharing" permissions

3. Build the project:
   ```bash
   mvn clean install
   ```
4. Run the server:
   ```bash
   mvn spring-boot:run
   ```

#### MCP Client

1. Navigate to `gcalendar-mcp-client`:
   ```bash
   cd gcalendar-mcp-client
   ```
2. Set up PostgreSQL database:
   
   **Option A: Using Docker Compose (Recommended)**
   ```bash
   # Start PostgreSQL using Docker Compose
   docker-compose -f .docker-compose/docker-compose.yaml up -d
   ```
   
   **Option B: Manual Setup**
   - Create a PostgreSQL database named `spring_ai`
   - Update database credentials in `src/main/resources/application.properties` if needed
3. Ensure the `OPENAI_API_KEY` environment variable is set (see Environment Setup above).
4. Build the project:
   ```bash
   mvn clean install
   ```
5. Run the client:
   ```bash
   mvn spring-boot:run
   ```

### Usage

- Interact with the system through API endpoints available in the MCP Client.
- Use the chat endpoint to communicate with the AI and manage calendar events based on user input.

## Configuration

### Environment Variables

- `OPENAI_API_KEY`: Your OpenAI API key (required for MCP Client)

### Application Settings

- **MCP Server** runs on port `8111` by default.
- **MCP Client** connects to server via SSE connection defined in `application.properties`.
- **Database**: PostgreSQL database `spring_ai` with credentials configured in `application.properties`.
- **AI Model**: Configured to use `openai/gpt-4o` by default (can be changed in `application.properties`).

## Contributing

Feel free to submit issues or pull requests if you have ideas on how to make this project more effective and robust.
