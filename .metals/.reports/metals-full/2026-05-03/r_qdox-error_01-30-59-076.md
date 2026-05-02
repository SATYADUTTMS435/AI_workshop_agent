error id: file://<WORKSPACE>/inverting_amp.java
file://<WORKSPACE>/inverting_amp.java
### com.thoughtworks.qdox.parser.ParseException: syntax error @[20,3]

error in qdox parser
file content:
```java
offset: 247
uri: file://<WORKSPACE>/inverting_amp.java
text:
```scala
import java.lang.Math;

class RValues {
	double R;
	double Rf;

	RValues(double R, double Rf) {
		this.R = R;
		this.Rf = Rf;
    	}
}

public class inverting_amp {
	public static RValues calculate(double f) {
		double gain = ;
		}
        }


		d@@ouble C = 1e-6;
		double R = 1 / (2 * Math.PI * Math.sqrt(6) * f * C);
		double Rf = gain * R;
		return new RCValues(R, Rf, C);
    	}

	public static String generateNetlist(RCValues v) {
        return String.format("""
$ 1 0.000015625 3.046768661252054 58 5 50
c 144 368 208 368 0 %e -2.544137674334456
c 208 368 272 368 0 %e -3.308245117520446
c 272 368 336 368 0 %e -0.9716832694680744
r 336 368 400 368 0 %f
r 208 368 208 448 0 %f
r 272 368 272 448 0 %f
g 208 448 208 464 0
g 272 448 272 464 0
a 400 384 496 384 0 15 -15 1000000 0.00006596531227299938 0
w 400 368 400 320 0
r 400 320 496 320 0 %f
w 496 320 496 384 0
w 496 384 512 384 0
w 512 384 512 288 0
w 512 288 144 288 0
w 144 288 144 368 0
g 400 400 400 448 0
""",
		v.C, v.C, v.C,
		v.R, v.R, v.R,
		v.Rf
		);
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Usage: java rc_phaseshift <frequency>");
			return;
		}

		double frequency = Double.parseDouble(args[0]);

		RCValues values = calculate(frequency);
		String netlist = generateNetlist(values);

		System.out.println("Generated Netlist:\n");
		System.out.println(netlist);
	}
}

```

```



#### Error stacktrace:

```
com.thoughtworks.qdox.parser.impl.Parser.yyerror(Parser.java:2025)
	com.thoughtworks.qdox.parser.impl.Parser.yyparse(Parser.java:2147)
	com.thoughtworks.qdox.parser.impl.Parser.parse(Parser.java:2006)
	com.thoughtworks.qdox.library.SourceLibrary.parse(SourceLibrary.java:232)
	com.thoughtworks.qdox.library.SourceLibrary.parse(SourceLibrary.java:190)
	com.thoughtworks.qdox.library.SourceLibrary.addSource(SourceLibrary.java:94)
	com.thoughtworks.qdox.library.SourceLibrary.addSource(SourceLibrary.java:89)
	com.thoughtworks.qdox.library.SortedClassLibraryBuilder.addSource(SortedClassLibraryBuilder.java:162)
	com.thoughtworks.qdox.JavaProjectBuilder.addSource(JavaProjectBuilder.java:174)
	scala.meta.internal.mtags.JavaMtags.indexRoot(JavaMtags.scala:49)
	scala.meta.internal.mtags.MtagsIndexer.index(MtagsIndexer.scala:22)
	scala.meta.internal.mtags.MtagsIndexer.index$(MtagsIndexer.scala:21)
	scala.meta.internal.mtags.JavaMtags.index(JavaMtags.scala:39)
	scala.meta.internal.mtags.Mtags$.allToplevels(Mtags.scala:155)
	scala.meta.internal.metals.DefinitionProvider.fromMtags(DefinitionProvider.scala:372)
	scala.meta.internal.metals.DefinitionProvider.$anonfun$positionOccurrence$6(DefinitionProvider.scala:291)
	scala.Option.orElse(Option.scala:477)
	scala.meta.internal.metals.DefinitionProvider.$anonfun$positionOccurrence$1(DefinitionProvider.scala:291)
	scala.Option.flatMap(Option.scala:283)
	scala.meta.internal.metals.DefinitionProvider.positionOccurrence(DefinitionProvider.scala:276)
	scala.meta.internal.metals.MetalsLspService.$anonfun$definitionOrReferences$1(MetalsLspService.scala:1736)
	scala.Option.map(Option.scala:242)
	scala.meta.internal.metals.MetalsLspService.definitionOrReferences(MetalsLspService.scala:1732)
	scala.meta.internal.metals.MetalsLspService.$anonfun$definition$1(MetalsLspService.scala:965)
	scala.meta.internal.metals.CancelTokens$.future(CancelTokens.scala:38)
	scala.meta.internal.metals.MetalsLspService.definition(MetalsLspService.scala:964)
	scala.meta.internal.metals.WorkspaceLspService.definition(WorkspaceLspService.scala:511)
	scala.meta.metals.lsp.DelegatingScalaService.definition(DelegatingScalaService.scala:65)
	java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:104)
	java.base/java.lang.reflect.Method.invoke(Method.java:565)
	org.eclipse.lsp4j.jsonrpc.services.GenericEndpoint.lambda$recursiveFindRpcMethods$0(GenericEndpoint.java:65)
	org.eclipse.lsp4j.jsonrpc.services.GenericEndpoint.request(GenericEndpoint.java:128)
	org.eclipse.lsp4j.jsonrpc.RemoteEndpoint.handleRequest(RemoteEndpoint.java:265)
	org.eclipse.lsp4j.jsonrpc.RemoteEndpoint.consume(RemoteEndpoint.java:195)
	org.eclipse.lsp4j.jsonrpc.json.StreamMessageProducer.handleMessage(StreamMessageProducer.java:189)
	org.eclipse.lsp4j.jsonrpc.json.StreamMessageProducer.listen(StreamMessageProducer.java:97)
	org.eclipse.lsp4j.jsonrpc.json.ConcurrentMessageProcessor.run(ConcurrentMessageProcessor.java:97)
	java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:545)
	java.base/java.util.concurrent.FutureTask.run(FutureTask.java:328)
	java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1090)
	java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:614)
	java.base/java.lang.Thread.run(Thread.java:1474)
```
#### Short summary: 

QDox parse error in file://<WORKSPACE>/inverting_amp.java