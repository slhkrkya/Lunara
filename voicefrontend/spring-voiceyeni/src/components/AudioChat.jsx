import React, { useRef, useState } from "react";
import { Client } from "@stomp/stompjs";

const App = () => {
  const [isConnected, setIsConnected] = useState(false);
  const localAudioRef = useRef(null);
  const remoteAudioRef = useRef(null);
  const peerConnection = useRef(null);
  const stompClient = useRef(null);
  const pendingCandidates = [];

  // STUN server configuration
  const configuration = {
    iceServers: [{ urls: "stun:stun.l.google.com:19302" }],
  };

  const connect = () => {
    console.log("Connect button clicked");
    stompClient.current = new Client({
      webSocketFactory: () => new WebSocket("ws://localhost:8089/audio-chat"),
      reconnectDelay: 5000,
      debug: (str) => console.log(str),
    });

    stompClient.current.onConnect = () => {
      console.log("Connected to WebSocket");
      setIsConnected(true);

      stompClient.current.subscribe("/topic/audio", (message) => {
        const signal = JSON.parse(message.body);
        console.log("Received message:", signal);
        handleSignal(signal);
      });
    };

    stompClient.current.onStompError = (frame) => {
      console.error("STOMP Error:", frame);
    };

    stompClient.current.activate();
    initializePeerConnection();
  };

  const initializePeerConnection = () => {
    peerConnection.current = new RTCPeerConnection(configuration);

    peerConnection.current.ontrack = (event) => {
      console.log("Track event received:", event);
      remoteAudioRef.current.srcObject = event.streams[0];
    };

    peerConnection.current.onicecandidate = (event) => {
      if (event.candidate) {
        console.log("Sending ICE candidate:", event.candidate);
        sendSignal({ type: "candidate", candidate: event.candidate });
      } else {
        console.log("All ICE candidates sent");
      }
    };
  };

  const sendSignal = (signal) => {
    if (stompClient.current && stompClient.current.connected) {
      stompClient.current.publish({
        destination: "/app/audio",
        body: JSON.stringify(signal),
      });
    }
  };

  const startAudio = async () => {
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
    localAudioRef.current.srcObject = stream;

    stream.getTracks().forEach((track) => {
      peerConnection.current.addTrack(track, stream);
    });

    const offer = await peerConnection.current.createOffer();
    await peerConnection.current.setLocalDescription(offer);

    sendSignal({ type: "offer", offer });
  };

  const handleOfferSignal = async (signal) => {
    if (peerConnection.current.signalingState !== "stable") {
      console.warn("Offer received, but signaling state is not stable. Retrying...");
      setTimeout(() => handleSignal(signal), 100);
      return;
    }

    console.log("Setting remote offer:", signal.offer);
    await peerConnection.current.setRemoteDescription(
      new RTCSessionDescription(signal.offer)
    );

    const answer = await peerConnection.current.createAnswer();
    await peerConnection.current.setLocalDescription(answer);

    sendSignal({ type: "answer", answer });
  };

  const handleCandidateSignal = async (signal) => {
    if (!peerConnection.current.remoteDescription) {
      console.warn("Remote description is null, queuing candidate.");
      pendingCandidates.push(signal.candidate);
      return;
    }

    console.log("Adding ICE candidate:", signal.candidate);
    await peerConnection.current.addIceCandidate(new RTCIceCandidate(signal.candidate));
  };

  const processPendingCandidates = async () => {
    while (pendingCandidates.length > 0) {
      const candidate = pendingCandidates.shift();
      console.log("Processing queued ICE candidate:", candidate);
      await peerConnection.current.addIceCandidate(new RTCIceCandidate(candidate));
    }
  };

  const handleSignal = async (signal) => {
    try {
      if (signal.type === "offer") {
        await handleOfferSignal(signal);
      } else if (signal.type === "answer") {
        if (peerConnection.current.signalingState !== "have-local-offer") {
          console.warn("Skipping answer because signaling state is not have-local-offer.");
          return;
        }
        console.log("Setting remote answer:", signal.answer);
        await peerConnection.current.setRemoteDescription(
          new RTCSessionDescription(signal.answer)
        );
        processPendingCandidates();
      } else if (signal.type === "candidate") {
        await handleCandidateSignal(signal);
      }
    } catch (error) {
      console.error("Error handling signal:", error);
    }
  };

  return (
    <div>
      <h1>Audio Chat</h1>
      {!isConnected && (
        <button onClick={connect}>Connect to WebSocket</button>
      )}
      {isConnected && <button onClick={startAudio}>Start Audio</button>}
      <audio ref={localAudioRef} autoPlay muted />
      <audio ref={remoteAudioRef} autoPlay />
    </div>
  );
};

export default App;
