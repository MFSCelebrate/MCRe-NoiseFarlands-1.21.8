/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.opengl.ARBBufferStorage
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL14
 *  org.lwjgl.opengl.GL30
 */
package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.lwjgl.opengl.ARBBufferStorage;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public class GlConst {
    final static public int GL_READ_FRAMEBUFFER = 36008;
    final static public int GL_DRAW_FRAMEBUFFER = 36009;
    final static public int GL_TRUE = 1;
    final static public int GL_FALSE = 0;
    final static public int GL_NONE = 0;
    final static public int GL_LINES = 1;
    final static public int GL_LINE_STRIP = 3;
    final static public int GL_TRIANGLE_STRIP = 5;
    final static public int GL_TRIANGLE_FAN = 6;
    final static public int GL_TRIANGLES = 4;
    final static public int GL_WRITE_ONLY = 35001;
    final static public int GL_READ_ONLY = 35000;
    final static public int GL_READ_WRITE = 35002;
    final static public int GL_MAP_READ_BIT = 1;
    final static public int GL_MAP_WRITE_BIT = 2;
    final static public int GL_EQUAL = 514;
    final static public int GL_LEQUAL = 515;
    final static public int GL_LESS = 513;
    final static public int GL_GREATER = 516;
    final static public int GL_GEQUAL = 518;
    final static public int GL_ALWAYS = 519;
    final static public int GL_TEXTURE_MAG_FILTER = 10240;
    final static public int GL_TEXTURE_MIN_FILTER = 10241;
    final static public int GL_TEXTURE_WRAP_S = 10242;
    final static public int GL_TEXTURE_WRAP_T = 10243;
    final static public int GL_NEAREST = 9728;
    final static public int GL_LINEAR = 9729;
    final static public int GL_NEAREST_MIPMAP_LINEAR = 9986;
    final static public int GL_LINEAR_MIPMAP_LINEAR = 9987;
    final static public int GL_CLAMP_TO_EDGE = 33071;
    final static public int GL_REPEAT = 10497;
    final static public int GL_FRONT = 1028;
    final static public int GL_FRONT_AND_BACK = 1032;
    final static public int GL_LINE = 6913;
    final static public int GL_FILL = 6914;
    final static public int GL_BYTE = 5120;
    final static public int GL_UNSIGNED_BYTE = 5121;
    final static public int GL_SHORT = 5122;
    final static public int GL_UNSIGNED_SHORT = 5123;
    final static public int GL_INT = 5124;
    final static public int GL_UNSIGNED_INT = 5125;
    final static public int GL_FLOAT = 5126;
    final static public int GL_ZERO = 0;
    final static public int GL_ONE = 1;
    final static public int GL_SRC_COLOR = 768;
    final static public int GL_ONE_MINUS_SRC_COLOR = 769;
    final static public int GL_SRC_ALPHA = 770;
    final static public int GL_ONE_MINUS_SRC_ALPHA = 771;
    final static public int GL_DST_ALPHA = 772;
    final static public int GL_ONE_MINUS_DST_ALPHA = 773;
    final static public int GL_DST_COLOR = 774;
    final static public int GL_ONE_MINUS_DST_COLOR = 775;
    final static public int GL_REPLACE = 7681;
    final static public int GL_DEPTH_BUFFER_BIT = 256;
    final static public int GL_COLOR_BUFFER_BIT = 16384;
    final static public int GL_RGBA8 = 32856;
    final static public int GL_PROXY_TEXTURE_2D = 32868;
    final static public int GL_RGBA = 6408;
    final static public int GL_TEXTURE_WIDTH = 4096;
    final static public int GL_BGR = 32992;
    final static public int GL_FUNC_ADD = 32774;
    final static public int GL_MIN = 32775;
    final static public int GL_MAX = 32776;
    final static public int GL_FUNC_SUBTRACT = 32778;
    final static public int GL_FUNC_REVERSE_SUBTRACT = 32779;
    final static public int GL_DEPTH_COMPONENT24 = 33190;
    final static public int GL_STATIC_DRAW = 35044;
    final static public int GL_DYNAMIC_DRAW = 35048;
    final static public int GL_STREAM_DRAW = 35040;
    final static public int GL_STATIC_READ = 35045;
    final static public int GL_DYNAMIC_READ = 35049;
    final static public int GL_STREAM_READ = 35041;
    final static public int GL_STATIC_COPY = 35046;
    final static public int GL_DYNAMIC_COPY = 35050;
    final static public int GL_STREAM_COPY = 35042;
    final static public int GL_SYNC_GPU_COMMANDS_COMPLETE = 37143;
    final static public int GL_TIMEOUT_EXPIRED = 37147;
    final static public int GL_WAIT_FAILED = 37149;
    final static public int GL_UNPACK_SWAP_BYTES = 3312;
    final static public int GL_UNPACK_LSB_FIRST = 3313;
    final static public int GL_UNPACK_ROW_LENGTH = 3314;
    final static public int GL_UNPACK_SKIP_ROWS = 3315;
    final static public int GL_UNPACK_SKIP_PIXELS = 3316;
    final static public int GL_UNPACK_ALIGNMENT = 3317;
    final static public int GL_PACK_ALIGNMENT = 3333;
    final static public int GL_PACK_ROW_LENGTH = 3330;
    final static public int GL_MAX_TEXTURE_SIZE = 3379;
    final static public int GL_TEXTURE_2D = 3553;
    final static public int[] CUBEMAP_TARGETS = new int[]{34069, 34070, 34071, 34072, 34073, 34074};
    final static public int GL_DEPTH_COMPONENT = 6402;
    final static public int GL_DEPTH_COMPONENT32 = 33191;
    final static public int GL_FRAMEBUFFER = 36160;
    final static public int GL_RENDERBUFFER = 36161;
    final static public int GL_COLOR_ATTACHMENT0 = 36064;
    final static public int GL_DEPTH_ATTACHMENT = 36096;
    final static public int GL_FRAMEBUFFER_COMPLETE = 36053;
    final static public int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
    final static public int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
    final static public int GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
    final static public int GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
    final static public int GL_FRAMEBUFFER_UNSUPPORTED = 36061;
    final static public int GL_LINK_STATUS = 35714;
    final static public int GL_COMPILE_STATUS = 35713;
    final static public int GL_VERTEX_SHADER = 35633;
    final static public int GL_FRAGMENT_SHADER = 35632;
    final static public int GL_TEXTURE0 = 33984;
    final static public int GL_TEXTURE1 = 33985;
    final static public int GL_TEXTURE2 = 33986;
    final static public int GL_DEPTH_TEXTURE_MODE = 34891;
    final static public int GL_TEXTURE_COMPARE_MODE = 34892;
    final static public int GL_ARRAY_BUFFER = 34962;
    final static public int GL_ELEMENT_ARRAY_BUFFER = 34963;
    final static public int GL_PIXEL_PACK_BUFFER = 35051;
    final static public int GL_COPY_READ_BUFFER = 36662;
    final static public int GL_COPY_WRITE_BUFFER = 36663;
    final static public int GL_PIXEL_UNPACK_BUFFER = 35052;
    final static public int GL_UNIFORM_BUFFER = 35345;
    final static public int GL_ALPHA_BIAS = 3357;
    final static public int GL_RGB = 6407;
    final static public int GL_RG = 33319;
    final static public int GL_R8 = 33321;
    final static public int GL_RED = 6403;
    final static public int GL_OUT_OF_MEMORY = 1285;

    public static int toGl(DepthTestFunction function) {
        return switch (function) {
            case DepthTestFunction.NO_DEPTH_TEST -> GL_ALWAYS;
            case DepthTestFunction.EQUAL_DEPTH_TEST -> GL_EQUAL;
            case DepthTestFunction.LESS_DEPTH_TEST -> GL_LESS;
            case DepthTestFunction.GREATER_DEPTH_TEST -> GL_GREATER;
            default -> GL_LEQUAL;
        };
    }

    public static int toGl(PolygonMode polygonMode) {
        return switch (polygonMode) {
            case PolygonMode.WIREFRAME -> GL_LINE;
            default -> GL_FILL;
        };
    }

    public static int toGl(DestFactor factor) {
        return switch (factor) {
            default -> throw new MatchException(null, null);
            case DestFactor.CONSTANT_ALPHA -> GL14.GL_CONSTANT_ALPHA;
            case DestFactor.CONSTANT_COLOR -> GL14.GL_CONSTANT_COLOR;
            case DestFactor.DST_ALPHA -> GL_DST_ALPHA;
            case DestFactor.DST_COLOR -> GL_DST_COLOR;
            case DestFactor.ONE -> GL_ONE;
            case DestFactor.ONE_MINUS_CONSTANT_ALPHA -> GL14.GL_ONE_MINUS_CONSTANT_ALPHA;
            case DestFactor.ONE_MINUS_CONSTANT_COLOR -> GL14.GL_ONE_MINUS_CONSTANT_COLOR;
            case DestFactor.ONE_MINUS_DST_ALPHA -> GL_ONE_MINUS_DST_ALPHA;
            case DestFactor.ONE_MINUS_DST_COLOR -> GL_ONE_MINUS_DST_COLOR;
            case DestFactor.ONE_MINUS_SRC_ALPHA -> GL_ONE_MINUS_SRC_ALPHA;
            case DestFactor.ONE_MINUS_SRC_COLOR -> GL_ONE_MINUS_SRC_COLOR;
            case DestFactor.SRC_ALPHA -> GL_SRC_ALPHA;
            case DestFactor.SRC_COLOR -> GL_SRC_COLOR;
            case DestFactor.ZERO -> GL_ZERO;
        };
    }

    public static int toGl(SourceFactor factor) {
        return switch (factor) {
            default -> throw new MatchException(null, null);
            case SourceFactor.CONSTANT_ALPHA -> GL14.GL_CONSTANT_ALPHA;
            case SourceFactor.CONSTANT_COLOR -> GL14.GL_CONSTANT_COLOR;
            case SourceFactor.DST_ALPHA -> GL_DST_ALPHA;
            case SourceFactor.DST_COLOR -> GL_DST_COLOR;
            case SourceFactor.ONE -> GL_ONE;
            case SourceFactor.ONE_MINUS_CONSTANT_ALPHA -> GL14.GL_ONE_MINUS_CONSTANT_ALPHA;
            case SourceFactor.ONE_MINUS_CONSTANT_COLOR -> GL14.GL_ONE_MINUS_CONSTANT_COLOR;
            case SourceFactor.ONE_MINUS_DST_ALPHA -> GL_ONE_MINUS_DST_ALPHA;
            case SourceFactor.ONE_MINUS_DST_COLOR -> GL_ONE_MINUS_DST_COLOR;
            case SourceFactor.ONE_MINUS_SRC_ALPHA -> GL_ONE_MINUS_SRC_ALPHA;
            case SourceFactor.ONE_MINUS_SRC_COLOR -> GL_ONE_MINUS_SRC_COLOR;
            case SourceFactor.SRC_ALPHA -> GL_SRC_ALPHA;
            case SourceFactor.SRC_ALPHA_SATURATE -> GL11.GL_SRC_ALPHA_SATURATE;
            case SourceFactor.SRC_COLOR -> GL_SRC_COLOR;
            case SourceFactor.ZERO -> GL_ZERO;
        };
    }

    public static int toGl(VertexFormat.DrawMode drawMode) {
        return switch (drawMode) {
            default -> throw new MatchException(null, null);
            case VertexFormat.DrawMode.LINES -> GL_TRIANGLES;
            case VertexFormat.DrawMode.LINE_STRIP -> GL_TRIANGLE_STRIP;
            case VertexFormat.DrawMode.DEBUG_LINES -> GL_LINES;
            case VertexFormat.DrawMode.DEBUG_LINE_STRIP -> GL_LINE_STRIP;
            case VertexFormat.DrawMode.TRIANGLES -> GL_TRIANGLES;
            case VertexFormat.DrawMode.TRIANGLE_STRIP -> GL_TRIANGLE_STRIP;
            case VertexFormat.DrawMode.TRIANGLE_FAN -> GL_TRIANGLE_FAN;
            case VertexFormat.DrawMode.QUADS -> GL_TRIANGLES;
        };
    }

    public static int toGl(VertexFormat.IndexType type) {
        return switch (type) {
            default -> throw new MatchException(null, null);
            case VertexFormat.IndexType.SHORT -> GL_UNSIGNED_SHORT;
            case VertexFormat.IndexType.INT -> GL_UNSIGNED_INT;
        };
    }

    public static int toGl(NativeImage.Format format) {
        return switch (format) {
            default -> throw new MatchException(null, null);
            case NativeImage.Format.RGBA -> GL_RGBA;
            case NativeImage.Format.RGB -> GL_RGB;
            case NativeImage.Format.LUMINANCE_ALPHA -> GL_RG;
            case NativeImage.Format.LUMINANCE -> GL_RED;
        };
    }

    public static int toGl(AddressMode addressMode) {
        return switch (addressMode) {
            default -> throw new MatchException(null, null);
            case AddressMode.REPEAT -> GL_REPEAT;
            case AddressMode.CLAMP_TO_EDGE -> GL_CLAMP_TO_EDGE;
        };
    }

    public static int toGl(VertexFormatElement.Type type) {
        return switch (type) {
            default -> throw new MatchException(null, null);
            case VertexFormatElement.Type.FLOAT -> GL_FLOAT;
            case VertexFormatElement.Type.UBYTE -> GL_UNSIGNED_BYTE;
            case VertexFormatElement.Type.BYTE -> GL_BYTE;
            case VertexFormatElement.Type.USHORT -> GL_UNSIGNED_SHORT;
            case VertexFormatElement.Type.SHORT -> GL_SHORT;
            case VertexFormatElement.Type.UINT -> GL_UNSIGNED_INT;
            case VertexFormatElement.Type.INT -> GL_INT;
        };
    }

    public static int toGlInternalId(TextureFormat format) {
        return switch (format) {
            default -> throw new MatchException(null, null);
            case TextureFormat.RGBA8 -> GL_RGBA8;
            case TextureFormat.RED8 -> GL_R8;
            case TextureFormat.RED8I -> GL30.GL_R8I;
            case TextureFormat.DEPTH32 -> GL_DEPTH_COMPONENT32;
        };
    }

    public static int toGlExternalId(TextureFormat format) {
        return switch (format) {
            default -> throw new MatchException(null, null);
            case TextureFormat.RGBA8 -> GL_RGBA;
            case TextureFormat.RED8 -> GL_RED;
            case TextureFormat.RED8I -> GL_RED;
            case TextureFormat.DEPTH32 -> GL_DEPTH_COMPONENT;
        };
    }

    public static int toGlType(TextureFormat format) {
        return switch (format) {
            default -> throw new MatchException(null, null);
            case TextureFormat.RGBA8 -> GL_UNSIGNED_BYTE;
            case TextureFormat.RED8 -> GL_UNSIGNED_BYTE;
            case TextureFormat.RED8I -> GL_UNSIGNED_BYTE;
            case TextureFormat.DEPTH32 -> GL_FLOAT;
        };
    }

    public static int toGl(ShaderType type) {
        return switch (type) {
            default -> throw new MatchException(null, null);
            case ShaderType.VERTEX -> GL_VERTEX_SHADER;
            case ShaderType.FRAGMENT -> GL_FRAGMENT_SHADER;
        };
    }

    public static int bufferUsageToGlFlag(int usage) {
        int i = 0;
        if ((usage & GpuBuffer.USAGE_MAP_READ) != 0) {
            i |= ARBBufferStorage.GL_MAP_PERSISTENT_BIT | GL30.GL_MAP_READ_BIT;
        }
        if ((usage & GpuBuffer.USAGE_MAP_WRITE) != 0) {
            i |= ARBBufferStorage.GL_MAP_PERSISTENT_BIT | GL30.GL_MAP_WRITE_BIT;
        }
        if ((usage & GpuBuffer.USAGE_COPY_DST) != 0) {
            i |= ARBBufferStorage.GL_DYNAMIC_STORAGE_BIT;
        }
        if ((usage & GpuBuffer.USAGE_HINT_CLIENT_STORAGE) != 0) {
            i |= ARBBufferStorage.GL_CLIENT_STORAGE_BIT;
        }
        return i;
    }

    public static int bufferUsageToGlEnum(int usage) {
        boolean bl;
        boolean bl2 = bl = (usage & GpuBuffer.USAGE_HINT_CLIENT_STORAGE) != 0;
        if ((usage & GpuBuffer.USAGE_MAP_WRITE) != 0) {
            if (bl) {
                return GL_STREAM_DRAW;
            }
            return GL_STATIC_DRAW;
        }
        if ((usage & GpuBuffer.USAGE_MAP_READ) != 0) {
            if (bl) {
                return GL_STREAM_READ;
            }
            return GL_STATIC_READ;
        }
        return GL_STATIC_DRAW;
    }
}

