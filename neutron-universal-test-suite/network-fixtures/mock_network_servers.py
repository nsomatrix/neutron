#!/usr/bin/env python3
"""
Neutron Universal Mock Network Servers
Provides deterministic local HTTP, TCP streaming socket, and UDP datagram servers
for fully offline, reproducible GCF networking validation.
"""

import sys
import time
import socket
import threading
from http.server import HTTPServer, BaseHTTPRequestHandler

HTTP_PORT = 18458
TCP_PORT = 18459
UDP_PORT = 18460

class MockHTTPHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path == "/status":
            self.send_response(200)
            self.send_header("Content-Type", "text/plain")
            self.send_header("X-Neutron-Test", "Active")
            self.end_headers()
            self.wfile.write(b"OK: Neutron HTTP Mock Active\n")
        elif self.path == "/data":
            self.send_response(200)
            self.send_header("Content-Type", "application/octet-stream")
            self.end_headers()
            self.wfile.write(b"SAMPLE-GCF-PAYLOAD-BINARY-DATA-0123456789\n")
        elif self.path == "/404":
            self.send_response(404)
            self.end_headers()
            self.wfile.write(b"Not Found\n")
        else:
            self.send_response(200)
            self.send_header("Content-Type", "text/plain")
            self.end_headers()
            self.wfile.write(f"ECHO: {self.path}\n".encode("utf-8"))

    def do_POST(self):
        content_length = int(self.headers.get("Content-Length", 0))
        body = self.rfile.read(content_length)
        self.send_response(200)
        self.send_header("Content-Type", "text/plain")
        self.end_headers()
        self.wfile.write(b"RECEIVED: " + body + b"\n")

    def log_message(self, format, *args):
        pass  # Suppress stdout clutter during testing

def run_http_server():
    server = HTTPServer(("127.0.0.1", HTTP_PORT), MockHTTPHandler)
    server.serve_forever()

def run_tcp_echo_server():
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    sock.bind(("127.0.0.1", TCP_PORT))
    sock.listen(5)
    while True:
        try:
            conn, addr = sock.accept()
            data = conn.recv(1024)
            if data:
                conn.sendall(b"TCP-ECHO:" + data)
            conn.close()
        except Exception:
            pass

def run_udp_echo_server():
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.bind(("127.0.0.1", UDP_PORT))
    while True:
        try:
            data, addr = sock.recvfrom(1024)
            if data:
                sock.sendto(b"UDP-ECHO:" + data, addr)
        except Exception:
            pass

def start_all_servers():
    t_http = threading.Thread(target=run_http_server, daemon=True)
    t_tcp = threading.Thread(target=run_tcp_echo_server, daemon=True)
    t_udp = threading.Thread(target=run_udp_echo_server, daemon=True)

    t_http.start()
    t_tcp.start()
    t_udp.start()

    print(f"Mock HTTP Server listening on http://127.0.0.1:{HTTP_PORT}")
    print(f"Mock TCP Socket Server listening on socket://127.0.0.1:{TCP_PORT}")
    print(f"Mock UDP Datagram Server listening on datagram://127.0.0.1:{UDP_PORT}")

if __name__ == "__main__":
    start_all_servers()
    print("All mock network servers started. Press Ctrl+C to terminate.")
    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        print("Shutting down servers.")
        sys.exit(0)

