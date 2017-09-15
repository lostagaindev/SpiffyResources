
/**
 * <br>
 * This will eventually be a AI system for general use.<br>
 * A decision-tree like thing. Very experimental.<br>
 * <br>
 * <br>
 * class concepts;<br>
 * <br>
 * Action                     - something a character can do<br>
 * Action.predictStatChange() - predicts the change of stats between when the action starts and ends.<br>
 * Action.do(delta)           - does the action based on time elipsed since last update. This do can make real stat changes.<br>
 * 
 * A action, for example, might be a basic movement from A to B.
 * <br>
 * 
 * ObjectiveSet                               - A set of objectives
 * ObjectiveSet.getObjectiveSetMode()         - either OR or AND
 * ObjectiveSet.getSubObjectives()            - other Objectives or ObjectiveSets that make up this
 * 
 * This allows objectives to be structured in combinations of ANDs and OR eg;
 *    (A or B) AND (D or C)
 * 
 * ObjectiveSet should be based on how semantic requests work or conditions work in the JAM.
 * 
 * Priorities                                  - A way to determine which action to take, where all actions forfill the objective equally well
 * 											   - think of it as a ordering method, rather then a way to determine a objective
 * 
 * Look into how Comparators maybe? Collection.Comparators() maybe?
 * 
 * -
 * 
 * Plan                                               - Control class that plans the final action to perform
 * Plan.getActionSet(Objective,Priorities,Character); - Returns a set of actions for a character to do.
 * 
 * ===
 * Process a actions but after each step run a check for "interupts" which can cause a replan
 * 
 * @author Tom *
 **/
package lostagain.nl.spiffyresources.client.spiffycore.ai;