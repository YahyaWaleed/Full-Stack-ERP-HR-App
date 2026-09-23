import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    // `npm run dev` with VITE_API_URL=/api/v1 talks to the backend through this proxy (same origin, like nginx in docker)
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
  test: {
    environment: 'jsdom',
    restoreMocks: true,
    unstubGlobals: true,
    setupFiles: ['./src/test/setup.js'],
  },
})
