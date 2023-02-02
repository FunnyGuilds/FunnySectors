package pl.rosehc.adapter.cuboid;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * @author stevimeister on 22/12/2021
 **/
public final class CuboidOutputStream extends OutputStream {

  private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
  private final byte xorBase;
  private byte[] buffer;
  private int count;
  private int position;

  public CuboidOutputStream(final byte xorBase) {
    this(32, xorBase);
  }

  public CuboidOutputStream(final int size, final byte xorBase) {
    if (size < 0) {
      throw new IllegalArgumentException("Negative initial size: " + size);
    }
    this.buffer = new byte[size];
    this.xorBase = xorBase;
  }

  private static int hugeCapacity(final int minCapacity) {
    if (minCapacity < 0) {
      throw new OutOfMemoryError();
    }
    return (minCapacity > MAX_ARRAY_SIZE)
        ? Integer.MAX_VALUE
        : MAX_ARRAY_SIZE;
  }

  private void ensureCapacity(final int minCapacity) {
    if (minCapacity - this.buffer.length > 0) {
      grow(minCapacity);
    }
  }

  private void grow(final int minCapacity) {
    int oldCapacity = this.buffer.length;
    int newCapacity = oldCapacity << 1;
    if (newCapacity - minCapacity < 0) {
      newCapacity = minCapacity;
    }
    if (newCapacity - MAX_ARRAY_SIZE > 0) {
      newCapacity = hugeCapacity(minCapacity);
    }
    this.buffer = Arrays.copyOf(this.buffer, newCapacity);
  }

  @Override
  public synchronized void write(final int bytes) {
    ensureCapacity(this.count + 1);
    this.buffer[this.count] = (byte) ((byte) bytes ^ (this.xorBase % (this.position + 1)));
    this.count += 1;
    this.position++;
  }

  @Override
  public synchronized void write(final byte[] bytes, final int offset, final int length) {
    if ((offset < 0) || (offset > bytes.length) || (length < 0) || ((offset + length) - bytes.length
        > 0)) {
      throw new IndexOutOfBoundsException();
    }
    ensureCapacity(this.count + length);
    for (int i = 0; i < bytes.length; i++) {
      bytes[i] = (byte) (bytes[i] ^ (this.xorBase % (this.position + 1)));
      this.position++;
    }
    System.arraycopy(bytes, offset, this.buffer, this.count, length);
    this.count += length;
  }

  public synchronized void writeTo(final OutputStream outputStream) throws IOException {
    outputStream.write(this.buffer, 0, this.count);
  }

  public synchronized void reset() {
    this.count = 0;
  }

  public synchronized byte[] toByteArray() {
    return Arrays.copyOf(this.buffer, this.count);
  }

  public synchronized int size() {
    return this.count;
  }

  @Override
  public synchronized String toString() {
    return new String(this.buffer, 0, this.count);
  }

  public synchronized String toString(final String charsetName)
      throws UnsupportedEncodingException {
    return new String(this.buffer, 0, this.count, charsetName);
  }
}