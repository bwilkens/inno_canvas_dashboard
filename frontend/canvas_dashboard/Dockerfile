FROM node:20-alpine AS build
WORKDIR /app

ARG VITE_AUTH_URL
ARG VITE_LOGOUT_URL

ENV VITE_AUTH_URL=${VITE_AUTH_URL}
ENV VITE_LOGOUT_URL=${VITE_LOGOUT_URL}

COPY . .
RUN npm ci
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80