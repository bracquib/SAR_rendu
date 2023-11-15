package info5.sar.utils;

public interface Listener {
    void received(byte[] msg);
    void closed();
	void received(Byte valueOf);
  }
