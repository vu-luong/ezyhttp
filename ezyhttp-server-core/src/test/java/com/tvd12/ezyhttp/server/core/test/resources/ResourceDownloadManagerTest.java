package com.tvd12.ezyhttp.server.core.test.resources;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.concurrent.EzyFutureMap;
import com.tvd12.ezyhttp.server.core.exception.MaxResourceDownloadCapacity;
import com.tvd12.ezyhttp.server.core.resources.ResourceDownloadManager;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;
import com.tvd12.test.reflect.FieldUtil;
import com.tvd12.test.util.RandomUtil;

public class ResourceDownloadManagerTest extends BaseTest {
	
	@Test
	public void drainTest() throws Exception {
		// given
		info("start drainTest");
		ResourceDownloadManager sut = new ResourceDownloadManager();
		
		int size = ResourceDownloadManager.DEFAULT_BUFFER_SIZE * 3;
		
		byte[] inputBytes = RandomUtil.randomByteArray(size);
		InputStream inputStream = new ByteArrayInputStream(inputBytes);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(size);
		
		// when
		sut.drain(inputStream, outputStream);
		
		// then
		byte[] outputBytes = outputStream.toByteArray();
		Asserts.assertEquals(inputBytes, outputBytes);
		sut.stop();
		info("end drainTest");
	}
	
	@Test
	public void drainFailedDueToOutputStream() throws Exception {
		// given
		info("start drainFailedDueToOutputStream");
		ResourceDownloadManager sut = new ResourceDownloadManager();
		
		int size = ResourceDownloadManager.DEFAULT_BUFFER_SIZE * 3;
		
		byte[] inputBytes = RandomUtil.randomByteArray(size);
		InputStream inputStream = new ByteArrayInputStream(inputBytes);
		
		OutputStream outputStream = mock(OutputStream.class);
		IOException exception = new IOException("just test");
		doThrow(exception).when(outputStream).write(any(byte[].class), anyInt(), anyInt());
		
		// when
		Throwable throwable = Asserts.assertThrows(() -> sut.drain(inputStream, outputStream));
		
		// then
		sut.stop();
		Asserts.assertEqualsType(throwable, IOException.class);
		info("finish drainFailedDueToOutputStream");
	}
	
	@Test
	public void drainFailedDueToOutputStreamDueToError() throws Exception {
		// given
		info("start drainFailedDueToOutputStreamDueToError");
		ResourceDownloadManager sut = new ResourceDownloadManager();
		
		BlockingQueue<Object> queue = FieldUtil.getFieldValue(sut, "queue");
		
		queue.offer(new Object());
		
		InputStream inputStream = mock(InputStream.class);
		OutputStream outputStream = mock(OutputStream.class);
		
		// when
		sut.drain(inputStream, outputStream);
		
		// then
		sut.stop();
		info("finish drainFailedDueToOutputStreamDueToError");
	}
	
	@Test
	public void activeFalse() throws Exception {
		// given
		info("start activeFalse");
		ResourceDownloadManager sut = new ResourceDownloadManager();
		FieldUtil.setFieldValue(sut, "active", false);
		
		int size = ResourceDownloadManager.DEFAULT_BUFFER_SIZE * 3;
		
		byte[] inputBytes = RandomUtil.randomByteArray(size);
		InputStream inputStream = new ByteArrayInputStream(inputBytes);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(size);
		
		// when
		sut.drain(inputStream, outputStream);
		
		// then
		byte[] outputBytes = outputStream.toByteArray();
		Asserts.assertEquals(inputBytes, outputBytes);
		sut.stop();
		info("finish activeFalse");
	}
	
	@Test
	public void drainFailedDueToMaxResourceUploadCapacity() throws Exception {
		// given
		info("start drainFailedDueToMaxResourceUploadCapacity");
		ResourceDownloadManager sut = new ResourceDownloadManager(1, 1, 1024);
		
		InputStream inputStream = mock(InputStream.class);
		when(inputStream.read(any(byte[].class))).thenReturn(10);
		OutputStream outputStream = mock(OutputStream.class);
		
		sut.stop();
		Thread.sleep(200);
		
		// when
		sut.drainAsync(inputStream, outputStream, it -> {});
		Throwable e = Asserts.assertThrows(() -> sut.drain(inputStream, outputStream));
		
		// then
		Asserts.assertThat(e).isEqualsType(MaxResourceDownloadCapacity.class);
		sut.stop();
		info("finsihed drainFailedDueToMaxResourceUploadCapacity");
	}
	
	@SuppressWarnings("rawtypes")
    @Test
    public void drainButFutureNull() throws Exception {
        // given
        info("start drainButFutureNull");
        ResourceDownloadManager sut = new ResourceDownloadManager(100, 1, 1024);
        
        EzyFutureMap futureMap = FieldUtil.getFieldValue(sut, "futureMap");
        
        InputStream inputStream = mock(InputStream.class);
        when(inputStream.read(any(byte[].class))).thenAnswer(it -> {
            Thread.sleep(200);
            return 0;
        });
        OutputStream outputStream = mock(OutputStream.class);
        
        // when
        sut.drainAsync(inputStream, outputStream, it -> {});
        futureMap.clear();
        
        // then
        Thread.sleep(300);
        sut.stop();
        info("finsihed drainButFutureNull");
    }
}
