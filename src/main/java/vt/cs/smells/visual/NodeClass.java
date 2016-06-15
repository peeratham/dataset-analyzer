package vt.cs.smells.visual;

import java.util.List;

public class NodeClass {
	String command;
	public List<Object> args;

	@Override
	public String toString() {
		String argStr = "";
		for(Object o: args){
			argStr+=o.toString();
		}
		return command+argStr;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj == null){
			return false;
		}
		final NodeClass other = (NodeClass) obj;
		if(!this.command.equals(other.command)){
			return false;
		}else{
			if(args.size()!=other.args.size()){
				return false;
			}
			for(int i = 0; i< args.size();i++){
				
				if(args.get(i)!=null && !args.get(i).equals(other.args.get(i))){
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public int hashCode(){
		int argsHash = 43;
		for(Object arg: args){
			if(arg!=null){
				argsHash += argsHash*arg.hashCode();
			}
		}
		return this.command.hashCode()*argsHash;
	}
	
	
}
