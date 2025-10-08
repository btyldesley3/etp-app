# Frontend

This is the **frontend application** for the event ticket platform project, built with
**React**, **TypeScript**, and **Vite**. It provides the user interface
and communicates with the backend APIs.

## Tech Stack

-   [React](https://react.dev/) +
    [TypeScript](https://www.typescriptlang.org/)\
-   [Vite](https://vitejs.dev/) for fast builds and hot reloading\
-   [Tailwind CSS](https://tailwindcss.com/) for styling\
-   [React Router](https://reactrouter.com/) for client-side routing\
-   [OIDC Client](https://github.com/authts/oidc-client-ts) for
    authentication\
-   [Lucide React](https://lucide.dev/) for icons\
-   [JSON Server](https://github.com/typicode/json-server) (via
    `db.json`) for local mock API data

## Project Structure

    FrontEnd/
    ├── public/             # Static assets served as-is
    ├── src/                # Application source code
    │   ├── assets/         # Images, fonts, and other static assets
    │   ├── components/     # Reusable UI components (buttons, forms, modals, etc.)
    │   ├── domain/         # Domain-specific models, DTOs, or feature logic
    │   ├── hooks/          # Custom React hooks for state or API handling
    │   ├── lib/            # Utility libraries (API clients, helpers, constants)
    │   ├── pages/          # Page-level components mapped to routes
    │   ├── index.css       # Global styles
    │   ├── main.tsx        # Application entry point
    │   └── vite-env.d.ts   # Vite/TypeScript type declarations
    ├── dist/               # Production build output
    ├── node_modules/       # Installed dependencies
    ├── components.json     # Shadcn/UI configuration
    ├── db.json             # JSON Server mock API
    ├── index.html          # HTML entry template
    ├── package.json        # Project metadata and dependencies
    ├── vite.config.ts      # Vite configuration
    └── tsconfig*.json      # TypeScript configurations

## Development

### Prerequisites

-   Node.js (\>= 18 recommended)\
-   npm or yarn

### Install dependencies

``` bash
npm install
```

### Run the development server

``` bash
npm run dev
```

App will be available at <http://localhost:5173>.

### Build for production

``` bash
npm run build
```

### Preview production build

``` bash
npm run preview
```

## Authentication

The project integrates **OIDC (OpenID Connect)** for authentication
using `react-oidc-context`. Update configuration in the codebase (`lib/`
or `config` depending on setup) with your identity provider details.

## Linting & Formatting

-   ESLint configuration: `eslint.config.js`\
-   Prettier configuration: `.prettierrc`
