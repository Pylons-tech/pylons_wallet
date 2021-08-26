package tech.pylons.ipc;

// Declare any non-default types here with import statements

interface IIpcInterface {

    String wallet2client();

    void client2wallet(String json);
}