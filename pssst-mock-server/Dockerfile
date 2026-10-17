FROM node:18-alpine

WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm ci --omit=dev

# Bundle app source
COPY . .

# Ensure data directory exists and set ownership
RUN mkdir -p /app/data && chown -R node:node /app

USER node

ENV NODE_ENV=production \
    PORT=8787 \
    DATA_DIR=/app/data

EXPOSE 8787

CMD ["node", "server.js"]
