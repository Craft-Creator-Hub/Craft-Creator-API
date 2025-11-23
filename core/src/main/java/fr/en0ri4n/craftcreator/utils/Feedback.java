package fr.en0ri4n.craftcreator.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Feedback
{
    ALREADY_EXISTS("serializer.message.recipe.exists"),
    DO_NOT_EXISTS("serializer.message.recipe.do_not_exists"),
    INVALID_JSON_RECIPE("serializer.message.recipe.invalid_json"),
    INVALID_BLOCK_DATA("serializer.message.recipe.invalid_block_data"),

    DATAPACK_ADDED("serializer.message.recipe.added"),
    DATAPACK_REMOVED("serializer.message.recipe.removed"),
    DATAPACK_PATH_INVALID("serializer.message.recipe.datapack_file_invalid"),
    DATAPACK_FILE_ERROR("serializer.message.recipe.datapack_file_error"),

    KUBEJS_ADDED("serializer.message.recipe.kubejs_added");

    private final String messageKey;
}
