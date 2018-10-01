import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.jiayang.util.config.Config;

public class ConfigTest {
@Test
public void test() {
	Map<String,Object> paths=(Map<String, Object>) Config.config.get("luaScript");
	System.out.println(((List)paths.get("globalPaths")).get(0));
	
}
}
