#FROM node:latest
#
#WORKDIR /app
#
#COPY package*.json ./
#
#RUN npm install
#
#COPY . .
#
#RUN npm run build
#
#
#CMD ["npm", "run", "start"]

# Stage 1: Install dependencies
#FROM node:alpine AS deps
#WORKDIR /app
#COPY package.json package-lock.json ./
#RUN npm ci
#
## Stage 2: Build the Next.js app
#FROM node:alpine AS builder
#WORKDIR /app
#COPY . .
#COPY --from=deps /app/node_modules ./node_modules
#RUN npm run build
#
## Stage 3: Prepare the production image
#FROM node:alpine AS runner
#WORKDIR /app
#
## Copy built files and necessary folders
#COPY --from=builder /app/.next ./.next
#COPY --from=builder /app/public ./public
#COPY --from=builder /app/node_modules ./node_modules
#COPY --from=builder /app/package.json ./package.json
#
## Expose the port Next.js will run on
#EXPOSE 3000
#
## Start the Next.js application
#CMD ["npm", "run", "start"]

FROM node:alpine AS base

# Install dependencies only when needed
FROM base AS deps
RUN apk add --no-cache libc6-compat
WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm install

# Rebuild the source code only when needed
FROM base AS builder
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .

RUN npm run build

# Production image, copy all the files and run next
FROM base AS runner
WORKDIR /app

ARG NEXT_PUBLIC_SPRING=http://gateway-service:8080
ARG NEXT_PUBLIC_SPRING_CLIENT=http://localhost:8080
ARG NEXTAUTH_URL=http://localhost:3000
ARG NEXTAUTH_URL_INTERNAL=http://nextjs:3000

ENV NEXT_PUBLIC_SPRING=$NEXT_PUBLIC_SPRING
ENV NEXT_PUBLIC_SPRING_CLIENT=$NEXT_PUBLIC_SPRING_CLIENT
ENV NEXTAUTH_URL=$NEXTAUTH_URL
ENV NEXTAUTH_URL_INTERNAL=$NEXTAUTH_URL_INTERNAL
ENV NODE_ENV=production

RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nextjs

COPY --from=builder /app/public ./public

RUN mkdir .next
RUN chown nextjs:nodejs .next

COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static

USER nextjs

EXPOSE 3000

ENV PORT=3000

CMD node server.js