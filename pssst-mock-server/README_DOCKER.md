# Pssst Mock Server (Docker)

Self-host the local Pssst sync server with persistent state storage.

## Quickstart

Run this single command from within the `pssst-mock-server` directory:

```bash
docker-compose up -d
```

To stop the server:
```bash
docker-compose down
```

## Configuration & Endpoints

- **Port**: `8787`
- **Host Base URL**: `http://localhost:8787`
- **Android Emulator URL**: `http://10.0.2.2:8787`
- **Persistence**: Player states are persisted across container restarts in the `pssst-data` volume (`/app/data/state.json`).

### Available Endpoints
- `GET /health`: Health check (`{"status":"ok","activeUsers":...}`)
- `POST /sync`: Push companion state (`Authorization: Bearer <UUID>`)
- `GET /state`: Pull companion state (`Authorization: Bearer <UUID>`)
