using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Formatters.Binary;
namespace CrossPlatformIPC
{
    [Serializable]
    public class Message
    {
        // This is a dropin from a stackoverflow comment, which is ok here but don't _release_ it w/ this in place
        sealed class CustomizedBinder : SerializationBinder
        {
            public override Type BindToType(string assemblyName, string typeName)
            {
                Type returntype = null;
                string sharedAssemblyName = "SharedAssembly, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null";
                assemblyName = Assembly.GetExecutingAssembly().FullName;
                typeName = typeName.Replace(sharedAssemblyName, assemblyName);
                returntype =
                        Type.GetType(String.Format("{0}, {1}",
                        typeName, assemblyName));

                return returntype;
            }

            public override void BindToName(Type serializedType, out string assemblyName, out string typeName)
            {
                base.BindToName(serializedType, out assemblyName, out typeName);
                assemblyName = "SharedAssembly, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null";
            }
        }

        public Dictionary<string, bool> bools;
        public Dictionary<string, bool[]> boolArrays;
        public Dictionary<string, byte> bytes;
        public Dictionary<string, byte[]> byteArrays;
        public Dictionary<string, char> chars;
        public Dictionary<string, char[]> charArrays;
        public Dictionary<string, double> doubles;
        public Dictionary<string, double[]> doubleArrays;
        public Dictionary<string, float> floats;
        public Dictionary<string, float[]> floatArrays;
        public Dictionary<string, int> ints;
        public Dictionary<string, int[]> intArrays;
        public Dictionary<string, long> longs;
        public Dictionary<string, long[]> longArrays;
        public Dictionary<string, short> shorts;
        public Dictionary<string, short[]> shortArrays;
        public Dictionary<string, string> strings;
        public Dictionary<string, string[]> stringArrays;

        public static Message Empty()
        {
            Message m = new Message();
            m.bools = new Dictionary<string, bool>();
            m.boolArrays = new Dictionary<string, bool[]>();
            m.bytes = new Dictionary<string, byte>();
            m.byteArrays = new Dictionary<string, byte[]>();
            m.chars = new Dictionary<string, char>();
            m.charArrays = new Dictionary<string, char[]>();
            m.doubles = new Dictionary<string, double>();
            m.doubleArrays = new Dictionary<string, double[]>();
            m.floats = new Dictionary<string, float>();
            m.floatArrays = new Dictionary<string, float[]>();
            m.ints = new Dictionary<string, int>();
            m.intArrays = new Dictionary<string, int[]>();
            m.longs = new Dictionary<string, long>();
            m.longArrays = new Dictionary<string, long[]>();
            m.shorts = new Dictionary<string, short>();
            m.shortArrays = new Dictionary<string, short[]>();
            m.strings = new Dictionary<string, string>();
            m.stringArrays = new Dictionary<string, string[]>();
            return m;
        }

        public static Message Deserialize(byte[] bytes)
        {
            BinaryFormatter f = new BinaryFormatter();
            f.Binder = new CustomizedBinder();
            return (Message)f.Deserialize(new MemoryStream(bytes));
        }

        public byte[] Serialize()
        {
            BinaryFormatter f = new BinaryFormatter();
            MemoryStream memoryStream = new MemoryStream();
            f.Serialize(memoryStream, this);
            return memoryStream.ToArray();
        }
    }
}