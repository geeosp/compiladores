package depend;

import java.util.Arrays;
import java.util.Iterator;

import com.ibm.wala.analysis.pointers.HeapGraph;
import com.ibm.wala.classLoader.IField;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CallGraphBuilderCancelException;
import com.ibm.wala.ipa.callgraph.impl.Everywhere;
import com.ibm.wala.ipa.callgraph.propagation.InstanceFieldKey;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.LocalPointerKey;
import com.ibm.wala.ipa.callgraph.propagation.PointerKey;
import com.ibm.wala.ssa.IR;

import depend.util.CallGraphGenerator;
import depend.util.Util;

public class PointsTo {
  
  static void procurarPonteirosAssociados(CallGraphGenerator cgg, MethodDependencyAnalysis mda)
      throws CallGraphBuilderCancelException {
    
    //pa = Pointer Analysis
    mda.pa = cgg.getPointerAnalysis();
    Iterator<PointerKey> it = mda.pa.getHeapModel().iteratePointerKeys();
    HeapGraph hgraph = mda.pa.getHeapGraph();
  
    while (it.hasNext()) {
      PointerKey pkey = it.next();
      
      if (pkey instanceof InstanceFieldKey) { 
    
        //pointer to a field associated with a set of instances.
        //Note that a single field can have multiple PointerKeys.
        InstanceFieldKey ifk = (InstanceFieldKey) pkey;
        
        //filter relevant fields
        if (ifk.getField().toString().contains(Util.APP_PREFIX)) {
          IField ifield = ifk.getField();
          //System.out.println("Analyzing the field/site: " + ifield + "--" + ifk.getInstanceKey());
          //arq.println("Analyzing the field/site: " + ifield + "--" + ifk.getInstanceKey());
        } else {
          continue;
        }
      } else if (pkey instanceof LocalPointerKey) {
       //pointer to a local
        LocalPointerKey lpk = (LocalPointerKey) pkey;
        IMethod lpkMethod = lpk.getNode().getMethod();
        
        //filter relevant fields
        //More info: http://wala.sourceforge.net/wiki/index.php/UserGuide:IR#Value_Numbering
        if (lpkMethod.toString().contains(Util.APP_PREFIX)) {
          IR ir = mda.cache.getIRFactory().makeIR(lpkMethod, Everywhere.EVERYWHERE,
              mda.options.getSSAOptions());
           
          //tinha um codigo comentado aqui. apaguei.
          
          
            String[] names = ir.getLocalNames(ir.getInstructions().length - 1, lpk.getValueNumber());
            System.out.println("Analyzing local variable " + Arrays.toString(names) + " in method " + lpkMethod);
            //arq.println("Analyzing local variable " + Arrays.toString(names) + " in method " + lpkMethod);
        } else {
          continue;
        }
      } else {
        continue;
      }
      
      //checa as dependências associadas
      
      Iterator<Object> pointedInstances = hgraph.getSuccNodes(pkey);
      while (pointedInstances.hasNext()) {
        InstanceKey ikey = (InstanceKey) pointedInstances.next();
        Iterator<Object> possibleAlias = hgraph.getPredNodes(ikey);

        while (possibleAlias.hasNext()) {
          PointerKey aliasPKey = (PointerKey) possibleAlias.next();
          if (!aliasPKey.equals(pkey)) {
            if (aliasPKey instanceof InstanceFieldKey) {
              InstanceFieldKey aliasIFK = (InstanceFieldKey) aliasPKey;
              IField aliasIField = aliasIFK.getField();
              System.out.println(" > possible alias: field " + aliasIField);
              //arq.println(" > possible alias: field " + aliasIField);
            } else if (aliasPKey instanceof LocalPointerKey) {
              LocalPointerKey aliasLPK = (LocalPointerKey) aliasPKey;
              IMethod lpkMethod = aliasLPK.getNode().getMethod();
              IR ir = mda.cache.getIRFactory().makeIR(lpkMethod, Everywhere.EVERYWHERE,
                  mda.options.getSSAOptions());
              String[] names = ir.getLocalNames(ir.getInstructions().length - 1, aliasLPK.getValueNumber());
              System.out.println(" > possible alias: local " + Arrays.toString(names) + " in method " + lpkMethod);
              //arq.println(" > possible alias: local " + Arrays.toString(names) + " in method " + lpkMethod);
            } else {
              System.out.println(" > unhandled pointer type: " + aliasPKey.getClass());
              //arq.println(" > unhandled pointer type: " + aliasPKey.getClass());
            }
          }
        }
      }
    }
  }

  private static PointerKey getPointerKey(CallGraphGenerator cgg, MethodDependencyAnalysis mda) throws CallGraphBuilderCancelException {
    
    //pa = Pointer Analysis
    mda.pa = cgg.getPointerAnalysis();
    Iterator<PointerKey> it = mda.pa.getHeapModel().iteratePointerKeys();
    HeapGraph hgraph = mda.pa.getHeapGraph();
  
    while (it.hasNext()) {
      PointerKey pkey = it.next();
      
      if (pkey instanceof InstanceFieldKey) { 
    
        //pointer to a field associated with a set of instances.
        //Note that a single field can have multiple PointerKeys.
        InstanceFieldKey ifk = (InstanceFieldKey) pkey;
        
        //filter relevant fields
        if (ifk.getField().toString().contains(Util.APP_PREFIX)) {
          IField ifield = ifk.getField();
          //System.out.println("Analyzing the field/site: " + ifield + "--" + ifk.getInstanceKey());
          //arq.println("Analyzing the field/site: " + ifield + "--" + ifk.getInstanceKey());
        } else {
          continue;
        }
      } else if (pkey instanceof LocalPointerKey) {
       //pointer to a local
        LocalPointerKey lpk = (LocalPointerKey) pkey;
        IMethod lpkMethod = lpk.getNode().getMethod();
        
        //filter relevant fields
        //More info: http://wala.sourceforge.net/wiki/index.php/UserGuide:IR#Value_Numbering
        if (lpkMethod.toString().contains(Util.APP_PREFIX)) {
          IR ir = mda.cache.getIRFactory().makeIR(lpkMethod, Everywhere.EVERYWHERE,
              mda.options.getSSAOptions());
           
          //tinha um codigo comentado aqui. apaguei.
          
          
            String[] names = ir.getLocalNames(ir.getInstructions().length - 1, lpk.getValueNumber());
            System.out.println("Analyzing local variable " + Arrays.toString(names) + " in method " + lpkMethod);
            //arq.println("Analyzing local variable " + Arrays.toString(names) + " in method " + lpkMethod);
        } else {
          continue;
        }
      } else {
        continue;
      }
    }  
  }

  
  

}