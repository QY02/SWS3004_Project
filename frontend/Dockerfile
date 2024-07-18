FROM node:lts-alpine AS build

WORKDIR /build

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build

FROM nginx:stable-alpine

COPY --from=build /build/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/

EXPOSE 80

ENTRYPOINT ["nginx", "-g", "daemon off;"]