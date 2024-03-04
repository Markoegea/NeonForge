package com.kingmarco.forge;

import com.kingmarco.observers.EventSystem;
import com.kingmarco.observers.Observer;
import com.kingmarco.observers.events.Event;
import com.kingmarco.observers.events.EventType;
import com.kingmarco.scenes.LevelEditorSceneInitializer;
import com.kingmarco.scenes.LevelGameSceneInitializer;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Objects;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

public class Sound implements Observer {
    private int bufferId;
    private int sourceId;
    private String filepath;
    private boolean loops;

    private boolean isPlaying = false;

    public Sound(String filepath, boolean loops){
        this.filepath = filepath;
        this.loops = loops;
        EventSystem.addObserver(this);
        init();
    }

    private void init(){
        //Allocate space to store the return information from stb
        stackPush();
        IntBuffer channelsBuffer = stackMallocInt(1);
        stackPush();
        IntBuffer sampleRateBuffer = stackMallocInt(1);

        ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(filepath, channelsBuffer, sampleRateBuffer);

        if (rawAudioBuffer == null){
            System.out.println("Could not load sound '" + filepath + "'");
            stackPop();
            stackPop();
            return;
        }

        // Retrive the extra information that was stored in the buffers by std
        int channels = channelsBuffer.get();
        int sampleRate = sampleRateBuffer.get();

        // Free
        stackPop();
        stackPop();

        // Find the correct openAl format
        int format = -1;
        if (channels == 1){
            format = AL_FORMAT_MONO16;
        } else if (channels == 2) {
            format = AL_FORMAT_STEREO16;
        }

        bufferId = alGenBuffers();
        alBufferData(bufferId, format, rawAudioBuffer, sampleRate);

        // Generate the source
        sourceId = alGenSources();

        alSourcei(sourceId, AL_BUFFER, bufferId);
        alSourcei(sourceId, AL_LOOPING, loops ? 1 : 0);
        alSourcei(sourceId, AL_POSITION, 0);
        alSourcef(sourceId, AL_GAIN, 0.3f);

        // Free stb raw audio buffer
        free(rawAudioBuffer);
    }

    public void changeSource(){
        stop();
        delete();
        init();
    }

    public void delete() {
        alDeleteSources(sourceId);
        alDeleteBuffers(bufferId);
    }

    public void play() {
        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
        if (state == AL_STOPPED){
            isPlaying = false;
            alSourcei(sourceId, AL_POSITION, 0);
        }

        if (!isPlaying){
            alSourcePlay(sourceId);
            isPlaying = true;
        }
    }

    public void stop() {
        if (isPlaying) {
            alSourceStop(sourceId);
            isPlaying = false;
        }
    }

    public String getFilepath() {
        return this.filepath;
    }

    public boolean isPlaying() {
        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
        if (state == AL_STOPPED) {
            isPlaying = false;
        }
        return isPlaying;
    }

    @Override
    public void onNotify(GameObject object, Event event) {
        if (event.type == EventType.AudioDeviceChanged) {
            changeSource();
        }
    }
}
