# eStatement Frontend

A Vite + Vue 3 frontend for the eStatement application. It provides UI for authentication, account statements, uploads, admin features and uses Pinia for state management and Vue Router for routing.

## Tech stack
- Vue 3
- Vite
- Pinia
- Vue Router
- Tailwind CSS
- Axios
- Vuelidate

## Requirements
- Node.js (see `package.json` engines). This project lists:
  - Node >= 20.19.0 or >= 22.12.0

## Quick start (PowerShell)
Install dependencies:

```powershell
npm install
```

Run the development server:

```powershell
npm run dev
```

Build for production:

```powershell
npm run build
```

Preview the built app locally:

```powershell
npm run preview
```

Lint and format:

```powershell
npm run lint
npm run format
```

## Tests
There are no test scripts defined in `package.json` at the moment. You can add a test runner (Vitest, Jest, etc.) and add a `test` script. Example with Vitest:

```json
// package.json (example)
"scripts": {
  "test": "vitest"
}
```

## Project structure (important files)
- `index.html` — app entry HTML
- `src/main.js` — app bootstrap
- `src/App.vue` — root component
- `src/router/` — Vue Router setup
- `src/store/` — Pinia stores
- `src/components/` — organized UI components
- `src/services/` — API and business services (uses Axios)
- `src/styles/` and `src/assets/` — stylesheets and assets

## Environment / API
This frontend talks to backend APIs through files in `src/services/`. The base URL or auth details may be configured in those service files (for example, look at `src/services/*.js`). Adjust accordingly for your backend environment.

## Adding a license / contributing
- If this is an open-source project, add a `LICENSE` file at the repo root.
- Add a `CONTRIBUTING.md` if you want contribution guidelines.

## Troubleshooting
- If you see errors about Node version, install a matching Node that satisfies `package.json` engines.
- If styles aren't applied, ensure Tailwind is built and `src/style.css` (or the imported CSS) is included.

## Next steps / suggestions
- Add a `test` script and tests (unit and integration). A minimal test already exists under `tests/unit/PasswordChange.spec.ts` — add a runner and script.
- Add CI (GitHub Actions) to run lint/build/tests on PRs.

---
Generated instructions assume the standard Vite setup and the scripts shown in `package.json` (dev/build/preview/lint/format). If you want, I can also add a simple `test` script and basic GitHub Actions workflow.
