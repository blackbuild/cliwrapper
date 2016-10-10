def output = args[-1] as File
output.mkdirs()
new File(output, "parameters.txt").text = args.join(" ")
