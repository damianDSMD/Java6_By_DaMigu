#!/usr/bin/env node
const fs = require('fs');
const path = require('path');

function parseArgs() {
  const args = {};
  const raw = process.argv.slice(2);
  for (let i = 0; i < raw.length; i++) {
    const a = raw[i];
    if (a.startsWith('--')) {
      const key = a.slice(2);
      const next = raw[i+1];
      if (next && !next.startsWith('--')) { args[key] = next; i++; }
      else { args[key] = true; }
    }
  }
  return args;
}

const FIRST = [
  'Nguyen','Tran','Le','Pham','Hoang','Phan','Vu','Vo','Dang','Bui'
];
const MIDDLE = [
  'Van','Thi','Quang','Minh','Thanh','Duc','Huy','Ngoc'
];
const LAST = [
  'An','Binh','Chau','Dung','Hanh','Hoa','Hung','Khanh','Linh','Son'
];
const ADDRS = [
  'Hanoi','Ho Chi Minh','Da Nang','Hai Phong','Can Tho','Nha Trang','Hue','Bien Hoa'
];

function rnd(arr) { return arr[Math.floor(Math.random()*arr.length)]; }

function makeName() {
  const useMiddle = Math.random() < 0.8;
  return `${rnd(FIRST)} ${useMiddle ? rnd(MIDDLE) + ' ' : ''}${rnd(LAST)}`;
}

function makeAddress() {
  return `${Math.floor(Math.random()*200)+1} ${rnd(['Le','Tran','Nguyen','Pham','Le'])} St, ${rnd(ADDRS)}`;
}

function generateRecord(i, options) {
  const maNVPrefix = options.prefix || 'NV';
  const id = options.pad ? String(i+options.start).padStart(4,'0') : String(i+options.start);
  const maNV = `${maNVPrefix}${id}`;
  const hoTen = options.names && options.names[i] ? options.names[i] : makeName();
  const diaChi = options.addresses && options.addresses[i] ? options.addresses[i] : makeAddress();
  const gioiTinh = Math.random() < 0.5; // boolean
  return { MaNV: maNV, HoTen: hoTen, DiaChi: diaChi, GioiTinh: gioiTinh };
}

function toCSV(rows) {
  const header = ['MaNV','HoTen','DiaChi','GioiTinh'];
  const lines = [header.join(',')];
  for (const r of rows) {
    const line = [r.MaNV, `"${r.HoTen.replace(/"/g,'""')}"`, `"${r.DiaChi.replace(/"/g,'""')}"`, r.GioiTinh];
    lines.push(line.join(','));
  }
  return lines.join('\n');
}

function toJSON(rows) {
  return JSON.stringify(rows, null, 2);
}

function main() {
  const argv = parseArgs();
  const count = Number(argv.count || argv.c || 10);
  const format = (argv.format || argv.f || 'csv').toLowerCase();
  const out = argv.out || argv.o || `postman_data.${format}`;
  const prefix = argv.prefix || argv.p || 'NV';
  const pad = argv.pad !== undefined ? argv.pad === 'true' || argv.pad === true : true;
  const start = Number(argv.start || 1);

  const opts = { prefix, pad, start };
  const rows = [];
  for (let i = 0; i < count; i++) rows.push(generateRecord(i, opts));

  const dir = path.dirname(out);
  if (dir && dir !== '.' && !fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true });

  if (format === 'csv') fs.writeFileSync(out, toCSV(rows), 'utf8');
  else fs.writeFileSync(out, toJSON(rows), 'utf8');

  console.log(`Wrote ${rows.length} records to ${out}`);
}

if (require.main === module) main();
