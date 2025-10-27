# Use Node.js 18 on a lightweight Alpine base
FROM node:18-alpine

# Set working directory
WORKDIR /app

# Copy dependency files first
COPY package*.json ./

# Install production dependencies
RUN npm install --omit=dev

# Copy the rest of the project files
COPY . .

# Expose app port
EXPOSE 3000

# Command to start your Node.js app
CMD ["node", "server.js"]
