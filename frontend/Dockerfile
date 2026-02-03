# syntax=docker/dockerfile:1

# Comments are provided throughout this file to help you get started.
# If you need more help, visit the Dockerfile reference guide at
# https://docs.docker.com/go/dockerfile-reference/

# Want to help us make this template better? Share your feedback here: https://forms.gle/ybq9Krt8jtBL3iCk7

ARG NODE_VERSION=24.12.0

FROM node:${NODE_VERSION}-alpine AS build

# Set working directory for all build stages.
WORKDIR /app

################################################################################
# Install dependencies
COPY package*.json ./
RUN npm ci

################################################################################
# Copy the rest of the source files into the image.
COPY . .

# Install Angular CLI
RUN npm install -g @angular/cli

# Run the build script.
RUN ng build --configuration production --prerender=false

################################################################################
# Stage 2: Serve with Nginx
FROM nginx:stable-alpine AS final

# Clean default Nginx html folder
RUN rm -rf /usr/share/nginx/html/*

# Copy Angular build (note: /browser is where index.html lives)
COPY --from=build /app/dist/frontend/browser/ /usr/share/nginx/html/

# Rename index.csr.html → index.html (so Nginx can serve it)
RUN if [ -f /usr/share/nginx/html/index.csr.html ]; then \
  mv /usr/share/nginx/html/index.csr.html /usr/share/nginx/html/index.html; \
  fi

# Copy custom nginx.conf if you have one
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose the port that the application listens on.
EXPOSE 80

# Run the application.
CMD ["nginx", "-g", "daemon off;"]
