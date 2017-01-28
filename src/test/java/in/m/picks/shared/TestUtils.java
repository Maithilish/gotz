package in.m.picks.shared;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TestUtils {

	public static Method getPrivateMethod(Class<?> clz, String name, Class<?>... args)
			throws SecurityException, NoSuchMethodException {
		Method method = clz.getDeclaredMethod(name, args);
		method.setAccessible(true);
		return method;
	}

	public static Field getPrivateField(Class<?> clz, String name)
			throws SecurityException, NoSuchFieldException {
		Field field = clz.getDeclaredField(name);
		field.setAccessible(true);
		return field;
	}

}
