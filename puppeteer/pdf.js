/**
 * Copyright 2017 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

const fs = require('fs');
const url = require('url');
const http = require('http');
const puppeteer = require('puppeteer');
const base64 = require('base64-js');

let PROTO_PATH = '/Users/user/IdeaProjects/bsu/oop-sandbox/src/main/proto/sandbox.proto';
let grpc = require('@grpc/grpc-js');
let protoLoader = require('@grpc/proto-loader');
// Suggested options for similarity to existing grpc.load behavior
let packageDefinition = protoLoader.loadSync(
    PROTO_PATH,
    {keepCase: true,
      longs: String,
      enums: String,
      defaults: true,
      oneofs: true
    });
let protoDescriptor = grpc.loadPackageDefinition(packageDefinition);
// The protoDescriptor object has the full package hierarchy
let PdfService = protoDescriptor.PdfService;
let PdfRequest = protoDescriptor.PdfRequest;
let PdfResponse = protoDescriptor.PdfResponse;
// console.log(PdfResponse)
// process.exit(0)

async function getPdf(call, callback) {
    (async (call) => {
        console.log(`handling ${call.request.url}`)
        const browser = await puppeteer.launch({
            headless: 'new',
            //executablePath: '/app/chrome/linux-117.0.5931.0/chrome-linux64/chrome',
            args: ['--no-sandbox']
        });
        const page = await browser.newPage();
        await page.goto(call.request.url, {
            waitUntil: 'networkidle2',
        });
        // page.pdf() is currently supported only in headless mode.
        // @see https://bugs.chromium.org/p/chromium/issues/detail?id=753118
        await page.pdf({
            path: 'hn.pdf',
            format: 'letter',
        });

        await browser.close();

        try {
            callback(null, { data: fs.readFileSync('hn.pdf').toString('base64') });
        } catch (e) {
            res.writeHead(500);
            res.end(JSON.stringify(e))
        }
    })(call);
}

let grpcServer = new grpc.Server();
grpcServer.addService(PdfService.service, {
  GetPdf: getPdf,
});
grpcServer.bindAsync('0.0.0.0:50051', grpc.ServerCredentials.createInsecure(), () => {
  grpcServer.start();
});
