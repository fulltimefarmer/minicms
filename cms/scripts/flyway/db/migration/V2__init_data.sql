INSERT INTO public.setting (id, setting_name, setting_type, string_value, boolean_value, number_value)
VALUES
    (1, 'setting1', 'STRING', 'default', null, null),
    (2, 'setting2', 'BOOLEAN', null, false, null),
    (3, 'setting3', 'NUMBER', null, null, 0);

INSERT INTO public.extend_setting (setting_id, setting_key, string_value, boolean_value, number_value)
VALUES
    (1, 'key1', 'value1', null, null),
    (1, 'key2', 'value2', null, null),
    (1, 'key3', 'value3', null, null),
    (2, 'key1', null, true, null),
    (2, 'key2', null, false, null),
    (2, 'key3', null, true, null),
    (3, 'key1', null, null, 100),
    (3, 'key2', null, null, 200),
    (3, 'key3', null, null, 300);