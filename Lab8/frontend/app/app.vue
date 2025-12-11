<template>
  <div>
    <h2>Chat</h2>

    <input v-model="username" placeholder="Username" />

    <div>
      <button :disabled="connected" @click="connect">Connect</button>
      <button :disabled="!connected" @click="disconnect">Disconnect</button>
    </div>

    <h3>Online Users</h3>
    <ul>
      <li v-for="u in users" :key="u">{{ u }}</li>
    </ul>

    <h3>Send Message</h3>
    <input v-model="message" placeholder="Message" />
    <button :disabled="!connected" @click="sendMessage">Send</button>

    <h3>Messages</h3>
    <div v-for="m in messages" :key="m.timestamp">
      {{ m.sender }}: {{ m.content }}
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import SockJS from "sockjs-client";
import Stomp from "stompjs";

const username = ref("");
const connected = ref(false);
const client = ref(null);
const message = ref("");
const messages = ref([]);
const users = ref([]);

function connect() {
  const socket = new SockJS("http://localhost:8080/ws");
  client.value = Stomp.over(socket);

  client.value.connect({}, () => {
    connected.value = true;

    // subscribe to messages
    client.value.subscribe("/topic/messages", (msg) => {
      messages.value.push(JSON.parse(msg.body));
    });

    // subscribe to online user list
    client.value.subscribe("/topic/users", (msg) => {
      const data = JSON.parse(msg.body);
      users.value = data.content ? data.content.split(",") : [];
    });

    // send username to backend
    client.value.send("/app/chat.username", {}, JSON.stringify({
      sender: username.value,
      type: "JOIN"
    }));
  });
}

function disconnect() {
  if (client.value) client.value.disconnect();
  connected.value = false;
  users.value = [];
}

function sendMessage() {
  client.value.send("/app/chat.send", {}, JSON.stringify({
    sender: username.value,
    content: message.value,
    type: "CHAT"
  }));
  message.value = "";
}
</script>
