package com.travelhub.travelhub_api.common.configuration;

import com.travelhub.travelhub_api.common.resource.TravelHubResource;
import com.travelhub.travelhub_api.data.mysql.entity.common.ServiceKeyEntity;
import com.travelhub.travelhub_api.data.mysql.repository.common.ServiceKeyRepository;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.util.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class Properties {

    private final Environment environment;
    private final ServiceKeyRepository serviceKeyRepository;

    private final Map<String, Object> properties = new HashMap<>();
    private final String CONFIG_FILE = "travel-hub.config.import";
    private String ymlPath = "";
    private long lastModified = 0;

    @PostConstruct
    public void init() throws Exception {
        ymlPath = environment.getProperty(CONFIG_FILE);
        log.info("{}", ymlPath);
        reloadYaml(System.currentTimeMillis());
        initServiceKey();
    }

    /**
     * 구글맵 키 정보 DB 이전
     */
    private void initServiceKey() {
        // 구글맵 키 정보
        ServiceKeyEntity target = serviceKeyRepository.findTopBy();
        TravelHubResource.googleMapKey = target.getSkMapKey();
    }

    public void reloadProperties() {

        try {
            Resource resource = new PathMatchingResourcePatternResolver().getResource(ymlPath);
            File file = ResourceUtils.getFile(resource.getURL());
            long currentModified = file.lastModified();

            // 파일에 변경된 사항이 있을때만 reload 한다.
            if (currentModified > lastModified) {
                reloadYaml(currentModified);
            }

        } catch (Exception e) {
            log.error("reloadProperties()", e);
        }
    }

    private void reloadYaml(long currentModified) throws Exception {
        Resource resource = new PathMatchingResourcePatternResolver().getResource(ymlPath);
        EncodedResource encodedResource = new EncodedResource(resource, "UTF-8");
        Constructor constructor = new Constructor(Map.class, new LoaderOptions());
        Yaml yaml = new Yaml(constructor);
        Map<String, Object> map = yaml.load(encodedResource.getInputStream());

        if (map == null) {
            return;
        }

        Map<String, Object> flattendMap = flatten(map);

        if (flattendMap.isEmpty()) {
            return;
        }

        synchronized (properties) {
            properties.clear();
            properties.putAll(flattendMap);
        }

        log.info("===== config yaml reloading... =====");

        for (String key : properties.keySet()) {
            log.info("{} : {}", key, properties.get(key));
        }

        log.info("===== config yaml reloading end. =====");

        lastModified = currentModified;
        TravelHubResource.STORAGE_CONFIG = get("storage.config.path", String.class, null);
        TravelHubResource.authEnabled = get("auth.enable", Boolean.class, null);
        TravelHubResource.testUser = get("auth.user", String.class, null);
        TravelHubResource.googleMapSearchFields = get("google.search-fields", String.class, null);
    }

    private Map<String, Object> flatten(Map<String, Object> map) {
        return flatten(map, "", new HashMap<>());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> flatten(Map<String, Object> map, String prefix, Map<String, Object> result) {
        map.forEach((key, value) -> {
            String newPrefix = prefix.isEmpty() ? key : prefix + "." + key;
            if (value instanceof Map) {
                flatten((Map<String, Object>) value, newPrefix, result);
            } else {
                result.put(newPrefix, value);
            }
        });
        return result;
    }

    public <T, V> T get(@NonNull String name, @NonNull Class<T> clazz, Class<V> insideClazz) {
        try {
            String input = "";

            synchronized (properties) {
                Object obj = properties.get(name);
                if (obj == null) {
                    return null;
                }

                input = String.valueOf(obj);
            }

            return makeValue(input, clazz, insideClazz);

        } catch (NullPointerException e) {
            log.error("properties value is not exist. key : {}", name, e);
            return null;	// key에 맞는 value가 없는 상태. null을 리턴한다.

        } catch (Exception e) {
            log.error("properties.get()", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private <T, V> T makeValue(String input, Class<T> clazz, Class<V> insideClazz) {

        if (clazz.equals(List.class)) {
            String[] split = input.split(",");
            List<V> list = new ArrayList<>();

            for (String s : split) {
                list.add(makeValue(s, insideClazz, null));
            }
            return (T) list;

        } else if (clazz.equals(Set.class)) {
            String[] split = input.split(",");
            Set<V> set = new HashSet<>();

            for (String s : split) {
                set.add(makeValue(s, insideClazz, null));
            }

            return (T) set;

        } else if (clazz.equals(Integer.class)) {
            return (T) Integer.valueOf(input);

        } else if (clazz.equals(Long.class)) {
            return (T) Long.valueOf(input);

        } else if (clazz.equals(Double.class)) {
            return (T) Double.valueOf(input);

        } else if (clazz.equals(Boolean.class)) {
            return (T) Boolean.valueOf(input);

        } else if (clazz.equals(String.class)) {
            return (T) input;

        } else {
            throw new IllegalArgumentException(clazz.getName() + " is not supported type.");
        }

    }
}
