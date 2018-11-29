using System;
using System.Collections.Generic;
using System.Linq;
using System.IO.Pipes;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Web;

namespace DevWallet
{
    class Program
    {
        enum State
        {
            Dead = -2,
            Error = -1,
            None = 0,
            WaitForHandshake = 1,
            OperationInProgress = 2,
            AwaitingMessage = 3,
        }

        private static TcpListener tcpListener = new TcpListener(IPAddress.Loopback, 50001);
        private static State state = State.WaitForHandshake;
        private static event EventHandler onReady;
        private static TcpClient client;
        static void Main(string[] args)
        {
            while (true)
            {
                switch (state)
                {
                    case State.Dead:
                        Environment.Exit(0);
                        break;
                    case State.Error:
                        Console.ReadLine();
                        Environment.Exit(-1);
                        break;
                    case State.WaitForHandshake:
                        if (CheckForHandshake()) state = State.AwaitingMessage;
                        else
                        {
                            Console.WriteLine("Handshake w/ client failed");
                            state = State.Error;
                        }
                        break;
                    case State.AwaitingMessage:
                        CrossPlatformIPC.Message msg;
                        if (CheckForIncomingMessage(out msg)) ProcessPylonsActionInMessage(msg);
                        else Thread.Sleep(250);
                        break;
                    //case State.OperationInProgress:
                        // Strictly speaking main thread should sleep, but rn this is single-threaded, so
                        // break;
                }
            }
        }

        private readonly static string handshakeMagic = "DEVWALLET_SERVER";
        private readonly static string handshakeReplyMagic = "DEVWALLET_CLIENT";

        private static bool NextMessageEqualsString (string str)
        {
            byte[] m = ReadNext();
            if (m == null) return false;
            string s = Encoding.ASCII.GetString(m);
            return s == str;
        }

        private static bool CheckForHandshake ()
        {
            tcpListener.Start();
            Console.WriteLine("Waiting for client...");

            while (!tcpListener.Pending()) Thread.Sleep(100);
            client = tcpListener.AcceptTcpClient();
            Console.WriteLine("Connected!");
            //Console.WriteLine(ReadNext()); // Throw it away - ensure pipe is empty before starting handshake
            WriteBytes(Encoding.ASCII.GetBytes(handshakeMagic));
            Console.WriteLine("Sent handshake magic, awaiting response");
            return NextMessageEqualsString(handshakeReplyMagic);
        }

        private static bool CheckForIncomingMessage (out CrossPlatformIPC.Message msg)
        {
            msg = ReadMessageFromPipe();
            if (msg != null) Console.WriteLine("Got incoming message!");
            return msg != null;
        }

        private static void ProcessPylonsActionInMessage (CrossPlatformIPC.Message msg)
        {
            if (!msg.strings.ContainsKey("pylonsAction"))
            {
                Console.WriteLine("Error: Incoming message did not contain a valid pylonsAction field.");
                state = State.Error;
            }
            else
            {
                string pylonsAction = msg.strings["pylonsAction"];
                Console.WriteLine("handling pylonsAction: " + pylonsAction);
                CrossPlatformIPC.Message outgoing = CrossPlatformIPC.Message.Empty();
                switch (pylonsAction)
                {
                    case "WALLET_SERVICE_TEST":
                        outgoing.strings["info"] = "dev wallet - svc test";
                        break;
                    case "WALLET_UI_TEST":
                        Console.Out.WriteLine("Please press the Any Key");
                        Console.ReadLine();
                        outgoing.strings["info"] = "dev wallet - ui test";
                        break;
                }
                Console.WriteLine("Sending outgoing message!");
                SendMessageToPipe(outgoing);
                Console.WriteLine("Message sent!");
            }
        }

        private static byte[] ReadNext ()
        {
            Console.WriteLine("Awaiting message from client...");
            NetworkStream s = client.GetStream();
            while (s.ReadByte() == -1) Thread.Sleep(100);
            byte[] buffer = new byte[1024 * 512]; // 512kb is overkill
            int len = s.Read(buffer, 0, buffer.Length);
            Console.WriteLine(len + " bytes down");
            if (len == 0) return null;
            byte[] m = new byte[len];
            for (int i = 0; i < len; i++) m[i] = buffer[i];
            return m;
        }

        private static void WriteBytes (byte[] bytes)
        {
            Console.WriteLine("Start write... (" + bytes.Length + ") bytes");
            NetworkStream s = client.GetStream();
            s.WriteByte(0xFF); // meaningless, just prepending message w/ an irrelevant byte so we can use ReadByte() to check for stream end w/o losing data
            s.Write(bytes, 0, bytes.Length);
            s.Flush();
            Console.WriteLine("Write complete");
        }

        private static CrossPlatformIPC.Message ReadMessageFromPipe() => CrossPlatformIPC.Message.Deserialize(ReadNext());

        private static void SendMessageToPipe(CrossPlatformIPC.Message m) => WriteBytes(m.Serialize());

    }
}
