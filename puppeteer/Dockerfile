FROM node:18

WORKDIR /app
COPY ./pdf.js /app/
COPY ./package.json /app/
RUN npx @puppeteer/browsers install chrome
RUN npm i

CMD ["node", "pdf.js"]