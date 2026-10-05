/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#f5f3ff',
          100: '#ede9fe',
          500: '#6c5ce7',
          600: '#5a4bd1',
          700: '#4834d4',
        },
        accent: {
          teal: '#00d2d3',
          green: '#2ed573',
          red: '#ff4757',
          orange: '#ffa502'
        },
        dark: {
          bg: '#0f172a',
          card: '#1e293b',
          surface: '#182234',
          border: '#334155'
        }
      }
    },
  },
  plugins: [],
}
