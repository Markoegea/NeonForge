package com.kingmarco.forge;

import com.kingmarco.observers.EventSystem;
import com.kingmarco.observers.Observer;
import com.kingmarco.observers.events.Event;
import com.kingmarco.observers.events.EventType;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

/**
 *  A class that storage the sounds to be played in the editor and game,
 *  and create the channels to play the sound in the audio devices.
 * */
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

    /**
     * Initializes the audio system, allocates space to store the return information from stb.
     * Decodes the audio file using stb_vorbis_decode_filename.
     * Retrieves the number of channels and the sample rate from the decoded audio data.
     * Determines the correct OpenAL format based on the number of channels.
     * Generates a buffer and a source in OpenAL, and assigns the decoded audio data to the buffer.
     * Sets various properties on the source, such as whether it should loop and its gain.
     * Frees the memory allocated for the raw audio data.
     *
     * @throws RuntimeException if the audio file could not be loaded.
     */
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

    /**
     * Stop the audio and change the audio source
     * */
    public void changeSource(){
        stop();
        delete();
        init();
    }

    /**
     * Delete the audio and release it from the memory
     * */
    public void delete() {
        alDeleteSources(sourceId);
        alDeleteBuffers(bufferId);
    }

    /**
     * Play the audio
     * */
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

    /**
     * Stop the audio
     * */
    public void stop() {
        if (isPlaying) {
            alSourceStop(sourceId);
            isPlaying = false;
        }
    }

    /**
     * Returns the file path of the audio file.
     *
     * @return The file path of the audio file.
     */
    public String getFilepath() {
        return this.filepath;
    }

    /**
     * Checks if the audio is currently playing.
     *
     * This method checks the state of the source in OpenAL. If the state is AL_STOPPED,
     * it sets the isPlaying flag to false.
     *
     * @return True if the audio is playing, false otherwise.
     */
    public boolean isPlaying() {
        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
        if (state == AL_STOPPED) {
            isPlaying = false;
        }
        return isPlaying;
    }

    /**
     * Handles notifications from game objects.
     *
     * This method is called when a game object sends a notification. If the event type
     * is AudioDeviceChanged, it calls the changeSource method to update the audio source.
     *
     * @param object The game object that sent the notification.
     * @param event The event that triggered the notification.
     */
    @Override
    public void onNotify(GameObject object, Event event) {
        if (event.type == EventType.AudioDeviceChanged) {
            changeSource();
        }
    }
}
