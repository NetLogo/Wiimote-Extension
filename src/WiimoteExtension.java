/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mwilkerson
 */
//if (remote.isAccelerometerEnabled())
//remote.setAccelerometerEnabled(false);
//if (remote.isSpeakerEnabled())
//remote.setSpeakerEnabled(false);
//if (remote.isIRSensorEnabled())
//remote.setIRSensorEnabled(false, WRIREvent.BASIC);

import java.util.List;

// For playing sound in wiimote speaker
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.nlogo.api.Argument;
import org.nlogo.api.ClassManager;
import org.nlogo.api.CompilerException;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.DefaultReporter;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.ExtensionManager;
import org.nlogo.api.ExtensionObject;
import org.nlogo.api.ImportErrorHandler;
import org.nlogo.api.LogoList;
import org.nlogo.api.LogoListBuilder;
import org.nlogo.api.PrimitiveManager;
import org.nlogo.api.Syntax;

import wiiremotej.PrebufferedSound;
import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteJ;
import wiiremotej.IRLight;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WiiRemoteAdapter;
import wiiremotej.event.WiiRemoteListener;


public class WiimoteExtension implements ClassManager {
  static WiiRemoteManager wiiRemoteManager = new WiiRemoteManager();
  //public static WiiRemote remote;

  public void load(PrimitiveManager primitiveManager) throws ExtensionException {
    // Connection and disconnection primitives
    primitiveManager.addPrimitive("connected?", new connected());
    primitiveManager.addPrimitive("connect", new connect());
    primitiveManager.addPrimitive("disconnect", new disconnect());
    primitiveManager.addPrimitive("ids", new getIds());
    primitiveManager.addPrimitive("number-connected", new numberConnected());

    // Inputs
    primitiveManager.addPrimitive("xacc", new xAccel());
    primitiveManager.addPrimitive("yacc", new yAccel());
    primitiveManager.addPrimitive("zacc", new zAccel());
    primitiveManager.addPrimitive("pitch", new pitch());
    primitiveManager.addPrimitive("roll", new roll());
    primitiveManager.addPrimitive("xir", new xIR());
    primitiveManager.addPrimitive("yir", new yIR());

    // Outputs
    primitiveManager.addPrimitive("led", new led());
    primitiveManager.addPrimitive("pulse", new pulse());
    primitiveManager.addPrimitive("vibrate", new vibrate());
    primitiveManager.addPrimitive("sound", new sound());
    primitiveManager.addPrimitive("a", new aPushed());
    primitiveManager.addPrimitive("b", new bPushed());
    primitiveManager.addPrimitive("1", new onePushed());
    primitiveManager.addPrimitive("2", new twoPushed());
    primitiveManager.addPrimitive("home", new homePushed());
    primitiveManager.addPrimitive("plus", new plusPushed());
    primitiveManager.addPrimitive("minus", new minusPushed());
    primitiveManager.addPrimitive("up", new upPushed());
    primitiveManager.addPrimitive("down", new downPushed());
    primitiveManager.addPrimitive("left", new leftPushed());
    primitiveManager.addPrimitive("right", new rightPushed());
  }

  public void unload(ExtensionManager extensionManager) throws ExtensionException {
    // 
  }

  /**
   * *******************
   * Connection Methods *
   * ********************
   */

  public static class connect extends org.nlogo.api.DefaultReporter {

    public Syntax getSyntax() {
      int ret = Syntax.NumberType();
      return Syntax.reporterSyntax(ret);
    }

    public Object report(Argument args[], Context context)
        throws ExtensionException {

      System.out.println("in connect");

      // We're gonna try connecting 5 times before
      // giving up. That way I don't have to keep starting
      // over and over again
      int i = 0;

      WiiRemote remote = null;

      while (i < 5 && remote == null) {
        try {

          remote = wiiRemoteManager.addRemote(WiiRemoteJ.findRemote());
          //remote.addWiiRemoteListener(new WRLImpl(remote));
          remote.setAccelerometerEnabled(true);
          //remote.setIRSensorEnabled(true);
          remote.setSpeakerEnabled(true);
//                    remote.setIRSensorEnabled(true);
          remote.setIRSensorEnabled(true, WRIREvent.BASIC);
          remote.setLEDIlluminated(0, true);
          remote.vibrateFor(1000);

          Integer index = wiiRemoteManager.getRemoteCount() - 1;

          return index.doubleValue();
          //remote.addWiiRemoteListener( new WiiRemoteListener() );
        } catch (Exception exc) {
          System.out.println(exc);
          // it may be that it finds it but doesn't fully communicate
          // if that's the case set it back to null and try again
        }

        i = i + 1;
      }

      return 999;

    }
  }

  public static class numberConnected extends DefaultReporter {
    public Syntax getSyntax() {
      int ret = Syntax.NumberType();
      return Syntax.reporterSyntax(ret);
    }

    public Object report(Argument args[], Context context)
        throws ExtensionException {

      return ((Integer) wiiRemoteManager.getRemoteCount()).doubleValue();

    }
  }

  public static class getIds extends DefaultReporter {
    public Syntax getSyntax() {
      int ret = Syntax.ListType();
      return Syntax.reporterSyntax(ret);
    }

    public Object report(Argument args[], Context context)
        throws ExtensionException {

      LogoListBuilder list = new LogoListBuilder();

      for (Integer i = 0; i < wiiRemoteManager.getRemoteCount(); i++) {
        if (wiiRemoteManager.getRemote(i).isConnected()) {
          list.add(i.doubleValue());
        }
      }

      return list.toLogoList();

    }
  }

  public static class disconnect extends DefaultCommand {
    public Syntax getSyntax() {
      int[] wiimoteNumber = {Syntax.NumberType()};
      return Syntax.commandSyntax(wiimoteNumber);
    }

    public void perform(Argument args[], Context context)
        throws ExtensionException {

      WiiRemote remote = getWiiRemote(args);
      // use typesafe helper method from
      // org.nlogo.api.Argument to access argument
      //WiiRemote remote = wiiRemoteManager.getRemote(wiimoteIndex);

      if (remote != null) {
        if (remote.isConnected()) {
          try {
            remote.setLEDIlluminated(0, false);
            remote.setLEDIlluminated(1, false);
            remote.setLEDIlluminated(2, false);
            remote.setLEDIlluminated(3, false);
          } catch (Exception exc) {
          }

          remote.disconnect();
        }
      }
    }
  }


  public static class connected extends DefaultReporter {
    public Syntax getSyntax() {
      int ret = Syntax.BooleanType();
      int[] input = {Syntax.NumberType()};
      return Syntax.reporterSyntax(
          new int[]{Syntax.NumberType()}, Syntax.BooleanType()
      );
    }

    public Object report(Argument args[], Context context)
        throws ExtensionException {
      if (getWiiRemote(args) != null) {
        return getWiiRemote(args).isConnected();
      }
      return false;
    }
  }

  public static class xAccel extends DefaultReporter {
    public Syntax getSyntax() {
      int ret = Syntax.NumberType();
      int[] input = {Syntax.NumberType()};
      return Syntax.reporterSyntax(input, ret);
    }

    public Object report(Argument args[], Context context)
        throws ExtensionException {
      WiiRemote remote = getWiiRemote(args);
      int index = getInt(args, 0);

      if (remote != null) {
        if (remote.isConnected() && remote.isAccelerometerEnabled()) {
          try {
            return new Double(wiiRemoteManager.getListener(index).xAccel);
          } catch (Exception exc) {
          }
        }
      }
      return new Double(0);
    }
  }

  public static class pulse extends DefaultCommand {

    public Syntax getSyntax() {
      int[] input = {Syntax.NumberType(),
          Syntax.NumberType(),
          Syntax.NumberType()};
      return Syntax.commandSyntax(input);
    }

    public void perform(Argument args[], Context context)
        throws ExtensionException {

      WiiRemote remote = getWiiRemote(args);

      if (remote != null) {
        if (remote.isConnected()) {
          try {
            int dur = args[1].getIntValue();
            int intv = args[2].getIntValue();
            remote.modulatedVibrateFor(dur, intv);
          } catch (Exception exc) {
          }
        }
      }
    }
  }

  public static class vibrate extends DefaultCommand {

    public Syntax getSyntax() {
      int[] input = {Syntax.NumberType(), Syntax.NumberType()};
      return Syntax.commandSyntax(input);
    }

    public void perform(Argument args[], Context context)
        throws ExtensionException {
      WiiRemote remote = getWiiRemote(args);

      if (remote != null) {
        if (remote.isConnected()) {
          try {
            remote.vibrateFor(args[1].getIntValue());
          } catch (Exception exc) {
          }
        }
      }
    }
  }

  public static class sound extends DefaultCommand {
    public Syntax getSyntax() {
      int[] input = {Syntax.NumberType(), Syntax.StringType()};
      return Syntax.commandSyntax(input);
    }

    public void perform(Argument args[], Context context)
        throws ExtensionException {

      WiiRemote remote = getWiiRemote(args);

      if (remote != null) {
        if (remote.isConnected()) {
          try {
            System.out.println("Buffering audio file...");
            long time = System.currentTimeMillis();
            AudioInputStream audio =
                AudioSystem.getAudioInputStream(
                    new java.io.File(args[1].getString())
                );
            PrebufferedSound prebuf = WiiRemote.bufferSound(audio);
            time = System.currentTimeMillis() - time;
            time /= 1000;
            System.out.println("Prebuf done: " + time + " seconds.");
            remote.playPrebufferedSound(prebuf, WiiRemote.SF_PCM8S);
          } catch (Exception exc) {
            System.out.println(exc);
          }
        }
      }
    }
  }

  public static class led extends DefaultCommand {
    public Syntax getSyntax() {
      int[] input = {Syntax.NumberType(), Syntax.NumberType()};
      return Syntax.commandSyntax(input);
    }

    public void perform(Argument args[], Context context)
        throws ExtensionException {
      WiiRemote remote = getWiiRemote(args);

      if (remote != null) {
        if (remote.isConnected()) {
          try {
            int lightNumber = args[1].getIntValue() - 1;
            remote.setLEDIlluminated(
                lightNumber,
                !(remote.isLEDIlluminated(lightNumber))
            );
          } catch (Exception exc) {
          }
        }
      }
    }
  }

  public static class pitch extends DefaultReporter {

    public Syntax getSyntax() {
      int ret = Syntax.NumberType();
      int[] input = {Syntax.NumberType()};
      return Syntax.reporterSyntax(input, ret);
    }

    public Object report(Argument args[], Context context)
        throws ExtensionException {

      WiiRemote remote = getWiiRemote(args);

      if (remote != null) {
        if (remote.isConnected() && remote.isAccelerometerEnabled()) {
          try {
            Double ret = new Double(
                wiiRemoteManager.getListener(remote).pitch);
            return (ret);
          } catch (Exception exc) {
            System.out.println(" Something broke: " + exc);
          }
        }
      }
      return new Double(0);
    }
  }


  public static class roll extends DefaultReporter {

    public Syntax getSyntax() {
      int ret = Syntax.NumberType();
      int[] input = {Syntax.NumberType()};
      return Syntax.reporterSyntax(input, ret);
    }

    public Object report(Argument args[], Context context)
        throws ExtensionException {

      WiiRemote remote = getWiiRemote(args);

      if (remote != null) {
        if (remote.isConnected() && remote.isAccelerometerEnabled()) {
          try {
            return new Double(
                wiiRemoteManager.getListener(remote).roll);
          } catch (Exception exc) {
          }
        }
      }
      return new Double(0);
    }
  }

  public static class yAccel extends DefaultReporter {

    public Syntax getSyntax() {
      int ret = Syntax.NumberType();
      int[] input = {Syntax.NumberType()};
      return Syntax.reporterSyntax(input, ret);
    }

    public Object report(Argument args[], Context context)
        throws ExtensionException {

      WiiRemote remote = getWiiRemote(args);

      if (remote != null) {
        if (remote.isConnected() && remote.isAccelerometerEnabled()) {
          try {
            return new Double(
                wiiRemoteManager.getListener(remote).yAccel);
          } catch (Exception exc) {
          }
        }
      }
      return new Double(0);
    }
  }

  public static class zAccel extends DefaultReporter {

    public Syntax getSyntax() {
      int ret = Syntax.NumberType();
      int[] input = {Syntax.NumberType()};
      return Syntax.reporterSyntax(input, ret);
    }

    public Object report(Argument args[], Context context)
        throws ExtensionException {

      WiiRemote remote = getWiiRemote(args);

      if (remote != null) {
        if (remote.isConnected() && remote.isAccelerometerEnabled()) {
          try {
            return new Double(
                wiiRemoteManager.getListener(remote).zAccel);
          } catch (Exception exc) {
          }
        }
      }
      return new Double(0);
    }
  }

  public static class xIR extends DefaultReporter {

    public Syntax getSyntax() {
      int ret = Syntax.NumberType();
      int[] input = {Syntax.NumberType()};
      return Syntax.reporterSyntax(input, ret);
    }

    public Object report(Argument args[], Context context)
        throws ExtensionException {

      WiiRemote remote = getWiiRemote(args);

      if (remote != null) {
        if (remote.isConnected() && remote.isIRSensorEnabled()) {
          try {
            return new Double(
                wiiRemoteManager.getListener(remote).xIR);
          } catch (Exception exc) {
          }
        }
      }
      return new Double(0);
    }
  }

  public static class yIR extends DefaultReporter {

    public Syntax getSyntax() {
      int ret = Syntax.NumberType();
      int[] input = {Syntax.NumberType()};
      return Syntax.reporterSyntax(input, ret);
    }

    public Object report(Argument args[], Context context)
        throws ExtensionException {

      WiiRemote remote = getWiiRemote(args);

      if (remote != null) {
        if (remote.isConnected() && remote.isIRSensorEnabled()) {
          try {
            return new Double(
                wiiRemoteManager.getListener(remote).yIR);
          } catch (Exception exc) {
          }
        }
      }
      return new Double(0);
    }
  }


  public static class bPushed extends DefaultReporter {

    public Syntax getSyntax() {
      int ret = Syntax.BooleanType();
      int[] input = {Syntax.NumberType()};
      return Syntax.reporterSyntax(input, ret);
    }

    public Object report(Argument args[], Context context)
        throws ExtensionException {

      WiiRemote remote = getWiiRemote(args);

      if (remote != null) {
        if (remote.isConnected()) {
          try {
            return wiiRemoteManager.getListener(remote).buttonB;
          } catch (Exception exc) {
          }
        }
      }
      return new Double(0);
    }
  }

  public static class aPushed extends DefaultReporter {

    public Syntax getSyntax() {
      int ret = Syntax.BooleanType();
      int[] input = {Syntax.NumberType()};
      return Syntax.reporterSyntax(input, ret);
    }

    public Object report(Argument args[], Context context)
        throws ExtensionException {

      WiiRemote remote = getWiiRemote(args);

      if (remote != null) {
        if (remote.isConnected()) {
          try {
            return wiiRemoteManager.getListener(remote).buttonA;
          } catch (Exception exc) {
          }
        }
      }
      return new Double(0);
    }
  }

  public static class plusPushed extends DefaultReporter {

    public Syntax getSyntax() {
      int ret = Syntax.BooleanType();
      int[] input = {Syntax.NumberType()};
      return Syntax.reporterSyntax(input, ret);
    }

    public Object report(Argument args[], Context context)
        throws ExtensionException {

      WiiRemote remote = getWiiRemote(args);

      if (remote != null) {
        if (remote.isConnected()) {
          try {
            return wiiRemoteManager.getListener(remote).buttonPlus;
          } catch (Exception exc) {
          }
        }
      }
      return new Double(0);
    }
  }

  public static class minusPushed extends DefaultReporter {

    public Syntax getSyntax() {
      int ret = Syntax.BooleanType();
      int[] input = {Syntax.NumberType()};
      return Syntax.reporterSyntax(input, ret);
    }

    public Object report(Argument args[], Context context)
        throws ExtensionException {

      WiiRemote remote = getWiiRemote(args);

      if (remote != null) {
        if (remote.isConnected()) {
          try {
            return wiiRemoteManager.getListener(remote).buttonMinus;
          } catch (Exception exc) {
          }
        }
      }
      return new Double(0);
    }
  }

  public static class homePushed extends DefaultReporter {

    public Syntax getSyntax() {
      int ret = Syntax.BooleanType();
      int[] input = {Syntax.NumberType()};
      return Syntax.reporterSyntax(input, ret);
    }

    public Object report(Argument args[], Context context)
        throws ExtensionException {

      WiiRemote remote = getWiiRemote(args);

      if (remote != null) {
        if (remote.isConnected()) {
          try {
            return wiiRemoteManager.getListener(remote).buttonHome;
          } catch (Exception exc) {
          }
        }
      }
      return new Double(0);
    }
  }

  public static class upPushed extends DefaultReporter {

    public Syntax getSyntax() {
      int ret = Syntax.BooleanType();
      int[] input = {Syntax.NumberType()};
      return Syntax.reporterSyntax(input, ret);
    }

    public Object report(Argument args[], Context context)
        throws ExtensionException {

      WiiRemote remote = getWiiRemote(args);

      if (remote != null) {
        if (remote.isConnected()) {
          try {
            return wiiRemoteManager.getListener(remote).buttonUp;
          } catch (Exception exc) {
          }
        }
      }
      return new Double(0);
    }
  }

  public static class downPushed extends DefaultReporter {

    public Syntax getSyntax() {
      int ret = Syntax.BooleanType();
      int[] input = {Syntax.NumberType()};
      return Syntax.reporterSyntax(input, ret);
    }

    public Object report(Argument args[], Context context)
        throws ExtensionException {

      WiiRemote remote = getWiiRemote(args);

      if (remote != null) {
        if (remote.isConnected()) {
          try {
            return wiiRemoteManager.getListener(remote).buttonDown;
          } catch (Exception exc) {
          }
        }
      }
      return new Double(0);
    }
  }

  public static class leftPushed extends DefaultReporter {

    public Syntax getSyntax() {
      int ret = Syntax.BooleanType();
      int[] input = {Syntax.NumberType()};
      return Syntax.reporterSyntax(input, ret);
    }

    public Object report(Argument args[], Context context)
        throws ExtensionException {

      WiiRemote remote = getWiiRemote(args);

      if (remote != null) {
        if (remote.isConnected()) {
          try {
            return wiiRemoteManager.getListener(remote).buttonLeft;
          } catch (Exception exc) {
          }
        }
      }
      return new Double(0);
    }
  }

  public static class rightPushed extends DefaultReporter {

    public Syntax getSyntax() {
      int ret = Syntax.BooleanType();
      int[] input = {Syntax.NumberType()};
      return Syntax.reporterSyntax(input, ret);
    }

    public Object report(Argument args[], Context context)
        throws ExtensionException {

      WiiRemote remote = getWiiRemote(args);

      if (remote != null) {
        if (remote.isConnected()) {
          try {
            return wiiRemoteManager.getListener(remote).buttonRight;
          } catch (Exception exc) {
          }
        }
      }
      return new Double(0);
    }
  }


  public static class onePushed extends DefaultReporter {

    public Syntax getSyntax() {
      int ret = Syntax.BooleanType();
      int[] input = {Syntax.NumberType()};
      return Syntax.reporterSyntax(input, ret);
    }

    public Object report(Argument args[], Context context)
        throws ExtensionException {

      WiiRemote remote = getWiiRemote(args);

      if (remote != null) {
        if (remote.isConnected()) {
          try {
            return wiiRemoteManager.getListener(remote).button1;
          } catch (Exception exc) {
          }
        }
      }
      return new Double(0);
    }
  }

  public static class twoPushed extends DefaultReporter {

    public Syntax getSyntax() {
      int ret = Syntax.BooleanType();
      int[] input = {Syntax.NumberType()};
      return Syntax.reporterSyntax(input, ret);
    }

    public Object report(Argument args[], Context context)
        throws ExtensionException {

      WiiRemote remote = getWiiRemote(args);

      if (remote != null) {
        if (remote.isConnected()) {
          try {
            return wiiRemoteManager.getListener(remote).button2;
          } catch (Exception exc) {
          }
        }
      }
      return new Double(0);
    }
  }

  // Utils

  // the first thing in args will always be the
  // remote index
  public static WiiRemote getWiiRemote(Argument args[]) throws ExtensionException {
    int wiimoteIndex;

    wiimoteIndex = getInt(args, 0);
    if (wiiRemoteManager.getRemoteCount() > wiimoteIndex) {
      return wiiRemoteManager.getRemote(wiimoteIndex);
    }

    return null;
  }

  public static int getInt(Argument args[], int index) throws ExtensionException {
    try {
      return args[index].getIntValue();
    } catch (Exception e) {
      throw new ExtensionException(e.getMessage());
    }
  }

  // Unimplemented stuff
  public void runOnce(ExtensionManager arg0) throws ExtensionException {
  }

  public void unload() throws ExtensionException {
  }

  public ExtensionObject readExtensionObject(ExtensionManager arg0,
                                             String arg1, String arg2) throws ExtensionException, CompilerException {
    return null;
  }

  public StringBuilder exportWorld() {
    return null;
  }

  public void importWorld(List<String[]> arg0, ExtensionManager arg1,
                          ImportErrorHandler arg2) throws ExtensionException {
  }

  public void clearAll() {
  }

  public List<String> additionalJars() {
    return null;
  }
}
