/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public final class AdvancementObtainedStatus
extends Enum<AdvancementObtainedStatus> {
    final static public AdvancementObtainedStatus OBTAINED = new AdvancementObtainedStatus(Identifier.ofVanilla("advancements/box_obtained"), Identifier.ofVanilla("advancements/task_frame_obtained"), Identifier.ofVanilla("advancements/challenge_frame_obtained"), Identifier.ofVanilla("advancements/goal_frame_obtained"));
    final static public AdvancementObtainedStatus UNOBTAINED = new AdvancementObtainedStatus(Identifier.ofVanilla("advancements/box_unobtained"), Identifier.ofVanilla("advancements/task_frame_unobtained"), Identifier.ofVanilla("advancements/challenge_frame_unobtained"), Identifier.ofVanilla("advancements/goal_frame_unobtained"));
    final private Identifier boxTexture;
    final private Identifier taskFrameTexture;
    final private Identifier challengeFrameTexture;
    final private Identifier goalFrameTexture;
    final static private AdvancementObtainedStatus[] field_2698;

    public static AdvancementObtainedStatus[] values() {
        return (AdvancementObtainedStatus[])field_2698.clone();
    }

    public static AdvancementObtainedStatus valueOf(String string) {
        return Enum.valueOf(AdvancementObtainedStatus.class, string);
    }

    private AdvancementObtainedStatus(Identifier boxTexture, Identifier taskFrameTexture, Identifier challengeFrameTexture, Identifier goalFrameTexture) {
        this.boxTexture = boxTexture;
        this.taskFrameTexture = taskFrameTexture;
        this.challengeFrameTexture = challengeFrameTexture;
        this.goalFrameTexture = goalFrameTexture;
    }

    public Identifier getBoxTexture() {
        return this.boxTexture;
    }

    public Identifier getFrameTexture(AdvancementFrame frame) {
        return switch (frame) {
            default -> throw new MatchException(null, null);
            case AdvancementFrame.TASK -> this.taskFrameTexture;
            case AdvancementFrame.CHALLENGE -> this.challengeFrameTexture;
            case AdvancementFrame.GOAL -> this.goalFrameTexture;
        };
    }

    private static AdvancementObtainedStatus[] method_36884() {
        return new AdvancementObtainedStatus[]{OBTAINED, UNOBTAINED};
    }

    static {
        field_2698 = AdvancementObtainedStatus.method_36884();
    }
}

